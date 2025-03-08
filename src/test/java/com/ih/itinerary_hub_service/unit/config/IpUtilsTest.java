package com.ih.itinerary_hub_service.unit.config;

import com.ih.itinerary_hub_service.config.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IpUtilsTest {

    @Test
    void hasIpAddress_shouldReturnTrue_whenIpMatches() throws NullPointerException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRemoteAddr()).thenReturn("192.168.1.1");

        RequestAuthorizationContext mockContext = mock(RequestAuthorizationContext.class);
        when(mockContext.getRequest()).thenReturn(mockRequest);

        AuthorizationManager<RequestAuthorizationContext> authorizationManager = IpUtils.hasIpAddress("192.168.1.1");

        boolean isValid = authorizationManager.authorize(null, mockContext).isGranted();

        assertTrue(isValid, "The IP address should match.");
    }

    @Test
    void hasIpAddress_shouldReturnFalse_whenIpDoesNotMatch() throws NullPointerException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRemoteAddr()).thenReturn("192.168.1.1");

        RequestAuthorizationContext mockContext = mock(RequestAuthorizationContext.class);
        when(mockContext.getRequest()).thenReturn(mockRequest);

        AuthorizationManager<RequestAuthorizationContext> authorizationManager = IpUtils.hasIpAddress("192.168.1.2");

        boolean isValid = authorizationManager.authorize(null, mockContext).isGranted();

        assertFalse(isValid, "The IP address should match.");
    }
}