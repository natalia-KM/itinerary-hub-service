package com.ih.itinerary_hub_service.config;

import com.ih.itinerary_hub_service.properties.CookieProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieMaker {

    private final CookieProperties cookieProperties;

    private static final String TOKEN_COOKIE_NAME = "access_token";
    private static final String USER_COOKIE_NAME = "user_id";

    public CookieMaker(CookieProperties cookieProperties) {
        this.cookieProperties = cookieProperties;
    }

    public void addDefaultCookies(HttpServletResponse response, String userId, String accessToken) {
        Cookie accessTokenCookie = makeCookie(TOKEN_COOKIE_NAME, accessToken);

        Cookie userIdCookie = makeCookie(USER_COOKIE_NAME, userId);
        userIdCookie.setMaxAge(60 * 60 * 24 * 30);

        response.addCookie(accessTokenCookie);
        response.addCookie(userIdCookie);
    }

    public void removeDefaultCookies(HttpServletResponse response) {
        Cookie tokenCookie = getCookieToRemove(TOKEN_COOKIE_NAME);
        Cookie userCookie = getCookieToRemove(USER_COOKIE_NAME);

        response.addCookie(tokenCookie);
        response.addCookie(userCookie);
    }

    public Cookie makeCookie(String cookieName, String cookieValue) {
        boolean isEnabled = cookieProperties.isEnabled();

        String sameSite = isEnabled ? "None" : "Lax";

        Cookie accessTokenCookie = new Cookie(cookieName, cookieValue);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(isEnabled);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setAttribute("SameSite", sameSite);

        return accessTokenCookie;
    }

    public Cookie getCookieToRemove(String cookieName) {
        boolean isEnabled = cookieProperties.isEnabled();

        String sameSite = isEnabled ? "None" : "Lax";

        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", sameSite);
        return cookie;
    }
}
