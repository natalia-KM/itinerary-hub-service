package com.ih.itinerary_hub_service.users.auth;

import com.ih.itinerary_hub_service.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class JwtAuthenticationFilterTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final String PROTECTED_URL = "/v1/users";
    private final String WHITELISTED_URL = "/v1/health";


    @Test
    void doFilterInternal_whenWhitelisted_allow() throws Exception {
        mockMvc.perform(get(WHITELISTED_URL))
                .andExpect(status().isOk())
                .andExpect(content().string("Itinerary Hub Service is up and running!"));
    }

    @Test
    void doFilterInternal_whenProtectedAndNoCookies_returnUnauthorized() throws Exception {
        mockMvc.perform(get(PROTECTED_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Missing authentication token or user ID"));
    }

    @Test
    void doFilterInternal_whenProtectedWithValidCookies_allow() throws Exception {
        mockMvc.perform(get(PROTECTED_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(guestUserAccessTokenCookie)
                        .cookie(guestUserIdCookie))
                .andExpect(status().isOk());
    }

    @Test
    void doFilterInternal_whenProtectedWithInvalidUserId_returnError() throws Exception {
        mockMvc.perform(get(PROTECTED_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(guestUserAccessTokenCookie)
                        .cookie(nonExistingUserIdCookie))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String reason = result.getResponse().getErrorMessage();
                    assertNotNull(reason);
                    assertTrue(reason.contains("User not found"), "Reason should contain 'User not found'");
                });
    }

    @Test
    void doFilterInternal_whenProtectedWithInvalidAccessToken_returnError() throws Exception {
        mockMvc.perform(get(PROTECTED_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(invalidAccessTokenCookie)
                        .cookie(guestUserIdCookie))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Invalid token"));
    }

    @Test
    void doFilterInternal_whenProtectedWithExpiredAccessToken_returnError() throws Exception {
        mockMvc.perform(get(PROTECTED_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(expiredAccessTokenCookie)
                        .cookie(guestUserIdCookie))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Token expired"))
                .andExpect(cookie().value("access_token", ""))
                .andExpect(cookie().value("user_id", ""));
    }
}