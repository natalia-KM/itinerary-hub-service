package com.ih.itinerary_hub_service.users.auth;

import com.ih.itinerary_hub_service.config.CookieMaker;
import com.ih.itinerary_hub_service.properties.ClientProperties;
import com.ih.itinerary_hub_service.users.exceptions.UserAlreadyExists;
import com.ih.itinerary_hub_service.users.exceptions.UserNotFoundException;
import com.ih.itinerary_hub_service.users.persistence.entity.User;
import com.ih.itinerary_hub_service.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final ClientProperties clientProperties;
    private final CookieMaker cookieMaker;

    public CustomAuthSuccessHandler(JwtService jwtService, UserService userService, @Lazy OAuth2AuthorizedClientService authorizedClientService, ClientProperties clientProperties, CookieMaker cookieMaker) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.authorizedClientService = authorizedClientService;
        this.clientProperties = clientProperties;
        this.cookieMaker = cookieMaker;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        final String BASE_URL = clientProperties.getBaseUrl();

        System.out.println("onAuthenticationSuccess start"); //TODO: remove
        System.out.println("onAuthenticationSuccess base url: " + BASE_URL); //TODO: remove

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        if (client == null || client.getAccessToken() == null) {
            System.out.println("onAuthenticationSuccess: redirect to login, due to client being null: " + BASE_URL); //TODO: remove
            response.sendRedirect(BASE_URL + "/login");
            return;
        }

        String googleAccessToken = client.getAccessToken().getTokenValue();
        String googleId = (String) attributes.get("sub"); // "sub" is the unique Google ID
        String firstName = (String) attributes.get("given_name");
        String lastName = (String) attributes.get("family_name");

        String userIdFromCookie = jwtService.getValueFromCookies(request, "user_id");
        UUID userId;

        try {
            if (userIdFromCookie != null) {
                userId = UUID.fromString(userIdFromCookie);
                userService.linkGuestAccountToGoogle(userId, googleId, googleAccessToken);
            } else {
                Optional<User> existingUser = userService.getUserByGoogleId(googleId);

                // Google allows for an account to have one name
                if(firstName == null || firstName.isEmpty()) {
                    firstName = "N/A";
                }

                if(lastName == null || lastName.isEmpty()) {
                    lastName = "N/A";
                }

                String finalFirstName = firstName;
                String finalLastName = lastName;

                userId = existingUser.map(User::getUserId)
                        .orElseGet(() -> userService.createGoogleUser(finalFirstName, finalLastName, googleId, googleAccessToken).getUserId());
            }
        } catch (UserAlreadyExists e) {
            System.out.println("onAuthenticationSuccess: send error User exists: " + BASE_URL); //TODO: remove
            response.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
            return;
        } catch (UserNotFoundException e) {
            System.out.println("onAuthenticationSuccess: send error User not found: " + BASE_URL); //TODO: remove
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            return;
        } catch (Exception e) {
            System.out.println("onAuthenticationSuccess: send error bad request: " + BASE_URL); //TODO: remove
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        String accessToken = jwtService.generateAccessToken(authentication.getName(), userId);
        cookieMaker.addDefaultCookies(response, userId.toString(), accessToken);

        System.out.println("onAuthenticationSuccess: success and redirect: " + BASE_URL); //TODO: remove
        response.sendRedirect(BASE_URL + "/dashboard");
    }
}
