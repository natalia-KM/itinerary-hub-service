package com.ih.itinerary_hub_service.unit.users;

import com.ih.itinerary_hub_service.users.auth.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void getValueFromCookies_whenCookieExists_returnValue() {
        String cookieName = "accessToken";
        String expectedValue = "test-token";
        Cookie[] cookies = { new Cookie(cookieName, expectedValue) };

        when(request.getCookies()).thenReturn(cookies);

        String result = jwtService.getValueFromCookies(request, cookieName);

        assertEquals(expectedValue, result);
    }

    @Test
    void getValueFromCookies_whenCookieDoesNotExist_returnNull() {
        when(request.getCookies()).thenReturn(null);

        String result = jwtService.getValueFromCookies(request, "missingCookie");

        assertNull(result);
    }

}