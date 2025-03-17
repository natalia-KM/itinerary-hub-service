package com.ih.itinerary_hub_service.users.auth;

import com.ih.itinerary_hub_service.config.CookieMaker;
import com.ih.itinerary_hub_service.users.persistence.entity.User;
import com.ih.itinerary_hub_service.users.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String[] WHITELIST = {
            "/v1/health",
            "/v1/users/guest",
            "/oauth2/authorization/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    private static final String TOKEN_COOKIE_NAME = "access_token";
    private static final String USER_COOKIE_NAME = "user_id";
    private final JwtService jwtService;
    private final UserService userService;
    private final CookieMaker cookieMaker;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserService userService, CookieMaker cookieMaker) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.cookieMaker = cookieMaker;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("doFilterInternal start"); //TODO: remove
        if (isWhitelisted(request)) {
            System.out.println("doFilterInternal whitelisted"); //TODO: remove
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtService.getValueFromCookies(request, TOKEN_COOKIE_NAME);
        String userId = jwtService.getValueFromCookies(request, USER_COOKIE_NAME);

        if (token == null || userId == null) {
            System.out.println("token || userId is null"); //TODO: remove
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing authentication token or user ID");
            return;
        }

        try {
            validateToken(token);

            User user = userService.getUserById(UUID.fromString(userId));

            request.setAttribute("userId", UUID.fromString(userId));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("doFilterInternal success"); //TODO: remove
        } catch (ExpiredJwtException e) {
            System.out.println("doFilterInternal expired token"); //TODO: remove

            cookieMaker.removeDefaultCookies(response);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
            return;
        } catch (IllegalArgumentException | NullPointerException | JwtException e) {
            System.out.println("doFilterInternal invalid token"); //TODO: remove
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        } catch (Exception e) {
            System.out.println("doFilterInternal exception"); //TODO: remove
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void validateToken(String token) {
        Jwts.parser()
                .verifyWith(jwtService.getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static boolean isWhitelisted(HttpServletRequest request) {
        for(String whitelisted : WHITELIST) {
            RequestMatcher requestMatcher = new AntPathRequestMatcher(whitelisted);
            if (requestMatcher.matches(request)) {
                return true;
            }

        }
        return false;
    }
}
