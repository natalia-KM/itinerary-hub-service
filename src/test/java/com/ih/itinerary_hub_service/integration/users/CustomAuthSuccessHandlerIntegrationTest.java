package com.ih.itinerary_hub_service.integration.users;

import com.ih.itinerary_hub_service.integration.BaseIntegrationTest;
import com.ih.itinerary_hub_service.users.auth.CustomAuthSuccessHandler;
import com.ih.itinerary_hub_service.users.auth.JwtService;
import com.ih.itinerary_hub_service.users.exceptions.UserAlreadyExists;
import com.ih.itinerary_hub_service.users.exceptions.UserNotFoundException;
import com.ih.itinerary_hub_service.users.persistence.entity.User;
import com.ih.itinerary_hub_service.users.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomAuthSuccessHandlerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CustomAuthSuccessHandler authSuccessHandler;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private static final String TOKEN_VALUE = "fake-token";
    private static final String SUB = "1234567890";
    private static final String USER_FIRST_NAME = "John";
    private static final String USER_LAST_NAME = "Doe";
    private static final String GOOGLE_ACCESS_TOKEN = "mock-access-token";
    private static final ArrayList<String> DEFAULT_COOKIES_NAMES = new ArrayList<>(List.of( "access_token", "user_id", "\n" +
            "JSESSIONID"));

    private final UUID uuidForUser = UUID.randomUUID();
    private final User newGoogleUser = new User(
            uuidForUser,
            USER_FIRST_NAME,
            USER_LAST_NAME,
            LocalDateTime.now(),
            false,
            SUB,
            null,
            GOOGLE_ACCESS_TOKEN
    );

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Nested
    class HappyPaths {

        private static Stream<Arguments> oidcTokens() {
            return Stream.of(
                    Arguments.of(getOidcIdTokenWithFullInfo(), USER_FIRST_NAME, USER_LAST_NAME),
                    Arguments.of(getOidcIdTokenWithFirstNameOnly(), USER_FIRST_NAME, "N/A"),
                    Arguments.of(getOidcIdTokenWithLastNameOnly(), "N/A", USER_LAST_NAME)
            );
        }

        @ParameterizedTest
        @MethodSource("oidcTokens")
        void withNewGoogleUser_thenHandleSuccess(OidcIdToken oidcIdToken, String firstName, String lastName) throws Exception {

            //given authorization on the provider side was successful
            OAuth2AuthenticationToken authToken = getOAuth2AuthenticationToken(oidcIdToken);
            OAuth2AuthorizedClient authorizedClient = getOAuth2AuthorizedClient(authToken);
            givenClientIsRegistered(authToken, authorizedClient);

            // when auth is called
            when(userService.createGoogleUser(any(), any(), any(), any())).thenReturn(newGoogleUser);
            when(jwtService.generateAccessToken(any(), any())).thenReturn(TOKEN_VALUE);

            authSuccessHandler.onAuthenticationSuccess(request, response, authToken);

            // returns redirect to the app
            assertEquals(302, response.getStatus());
            assertEquals("http://localhost:3000/dashboard", response.getRedirectedUrl());

            verify(userService, times(1)).createGoogleUser(firstName, lastName, SUB, GOOGLE_ACCESS_TOKEN);

            thenResponseShouldIncludeDefaultCookies(response);
        }

        @Test
        void withExistingGoogleUser_thenHandleSuccess() throws Exception {

            //given authorization on the provider side was successful
            OidcIdToken oidcIdToken = getOidcIdTokenWithFullInfo();
            OAuth2AuthenticationToken authToken = getOAuth2AuthenticationToken(oidcIdToken);
            OAuth2AuthorizedClient authorizedClient = getOAuth2AuthorizedClient(authToken);
            givenClientIsRegistered(authToken, authorizedClient);

            // when auth is called with existing google user
            when(userService.getUserByGoogleId(any())).thenReturn(Optional.of(newGoogleUser));
            when(userService.getUserById(newGoogleUser.getUserId())).thenReturn(newGoogleUser);

            when(jwtService.generateAccessToken(any(), any())).thenReturn(TOKEN_VALUE);

            authSuccessHandler.onAuthenticationSuccess(request, response, authToken);

            assertEquals(302, response.getStatus());
            assertEquals("http://localhost:3000/dashboard", response.getRedirectedUrl());

            verify(userService, never()).createGoogleUser(any(), any(), any(), any());

            thenResponseShouldIncludeDefaultCookies(response);
        }

        @Test
        void withGuestUser_thenHandleSuccess() throws Exception {
            //given authorization on the provider side was successful
            OidcIdToken oidcIdToken = getOidcIdTokenWithFullInfo();
            OAuth2AuthenticationToken authToken = getOAuth2AuthenticationToken(oidcIdToken);
            OAuth2AuthorizedClient authorizedClient = getOAuth2AuthorizedClient(authToken);
            givenClientIsRegistered(authToken, authorizedClient);

            // when auth is called with existing default cookies
            when(jwtService.getValueFromCookies(any(), any())).thenReturn(String.valueOf(uuidForUser));
            when(jwtService.generateAccessToken(any(), any())).thenReturn(TOKEN_VALUE);

            authSuccessHandler.onAuthenticationSuccess(request, response, authToken);

            // returns redirect to the app
            assertEquals(302, response.getStatus());
            assertEquals("http://localhost:3000/dashboard", response.getRedirectedUrl());

            verify(userService, times(1)).linkGuestAccountToGoogle(any(), any(), any());

            thenResponseShouldIncludeDefaultCookies(response);
        }

    }

    @Nested
    class SadPaths {
        @Test
        void withGuestUser_whenUserNotFound_thenHandleException() throws Exception {
            //given authorization on the provider side was successful
            OidcIdToken oidcIdToken = getOidcIdTokenWithFullInfo();
            OAuth2AuthenticationToken authToken = getOAuth2AuthenticationToken(oidcIdToken);
            OAuth2AuthorizedClient authorizedClient = getOAuth2AuthorizedClient(authToken);
            givenClientIsRegistered(authToken, authorizedClient);

            // when auth is called and user is not found
            when(jwtService.getValueFromCookies(any(), any())).thenReturn(String.valueOf(uuidForUser));
            when(jwtService.generateAccessToken(any(), any())).thenReturn(TOKEN_VALUE);
            doThrow(UserNotFoundException.class).when(userService).linkGuestAccountToGoogle(any(), any(), any());

            authSuccessHandler.onAuthenticationSuccess(request, response, authToken);

            // returns redirect to the login page
            assertEquals(404, response.getStatus());

            verify(userService, times(1)).linkGuestAccountToGoogle(any(), any(), any());
            assertEquals(0, response.getCookies().length);
        }

        @Test
        void withGuestUser_whenGoogleAccountIsAlreadyLinked_thenHandleException() throws Exception {
            //given authorization on the provider side was successful
            OidcIdToken oidcIdToken = getOidcIdTokenWithFullInfo();
            OAuth2AuthenticationToken authToken = getOAuth2AuthenticationToken(oidcIdToken);
            OAuth2AuthorizedClient authorizedClient = getOAuth2AuthorizedClient(authToken);
            givenClientIsRegistered(authToken, authorizedClient);

            // when auth is called and user is not found
            when(jwtService.getValueFromCookies(any(), any())).thenReturn(String.valueOf(uuidForUser));
            when(jwtService.generateAccessToken(any(), any())).thenReturn(TOKEN_VALUE);
            doThrow(UserAlreadyExists.class).when(userService).linkGuestAccountToGoogle(any(), any(), any());

            authSuccessHandler.onAuthenticationSuccess(request, response, authToken);

            // returns redirect to the login page
            assertEquals(409, response.getStatus());

            verify(userService, times(1)).linkGuestAccountToGoogle(any(), any(), any());
            assertEquals(0, response.getCookies().length);
        }
    }

    private void givenClientIsRegistered(OAuth2AuthenticationToken authToken, OAuth2AuthorizedClient authorizedClient) {
        when(authorizedClientService.loadAuthorizedClient(eq("google"), eq(authToken.getName())))
                .thenReturn(authorizedClient);
    }

    private void thenResponseShouldIncludeDefaultCookies(MockHttpServletResponse response) {
        Cookie[] cookies = response.getCookies();
        assertTrue(cookies.length > 1);

        for (Cookie cookie : cookies) {
            assertTrue(DEFAULT_COOKIES_NAMES.contains(cookie.getName()));
            assertFalse(cookie.getValue().isEmpty());
        }
    }

    private static OAuth2AuthorizedClient getOAuth2AuthorizedClient(OAuth2AuthenticationToken authToken) {
        return new OAuth2AuthorizedClient(
                ClientRegistration.withRegistrationId("google")
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                        .tokenUri("https://oauth2.googleapis.com/token")
                        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                        .clientId("client-id")
                        .clientSecret("client-secret")
                        .build(),
                authToken.getName(),
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, GOOGLE_ACCESS_TOKEN,
                        Instant.now(), Instant.now().plus(Duration.ofHours(1)))
        );
    }

    private static OAuth2AuthenticationToken getOAuth2AuthenticationToken(OidcIdToken idToken) {
        OidcUser oidcUser = new DefaultOidcUser(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                idToken
        );

        return new OAuth2AuthenticationToken(oidcUser, oidcUser.getAuthorities(), "google");
    }

    public static OidcIdToken getOidcIdTokenWithFullInfo() {
        return OidcIdToken
                .withTokenValue(TOKEN_VALUE)
                .claim("sub", SUB)
                .claim("given_name", USER_FIRST_NAME)
                .claim("family_name", USER_LAST_NAME)
                .build();
    }

    private static OidcIdToken getOidcIdTokenWithFirstNameOnly() {
        return OidcIdToken
                .withTokenValue(TOKEN_VALUE)
                .claim("sub", SUB)
                .claim("given_name", USER_FIRST_NAME)
                .build();
    }

    private static OidcIdToken getOidcIdTokenWithLastNameOnly() {
        return OidcIdToken
                .withTokenValue(TOKEN_VALUE)
                .claim("sub", SUB)
                .claim("family_name", USER_LAST_NAME)
                .build();
    }



}