package com.ih.itinerary_hub_service.integration.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ih.itinerary_hub_service.integration.BaseIntegrationTest;
import com.ih.itinerary_hub_service.users.persistence.repository.UserRepository;
import com.ih.itinerary_hub_service.users.requests.UpdateUserDetailsRequest;
import com.ih.itinerary_hub_service.users.requests.UserDetailsRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Nested
    class CreateUser {
        @Test
        void createUser_whenValidRequest_returnStatusCreated() throws Exception {
            UserDetailsRequest request = new UserDetailsRequest("John", "Doe");

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/users/guest")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(cookie().exists("access_token"))
                    .andExpect(cookie().value("access_token", not(emptyOrNullString())))
                    .andExpect(cookie().exists("user_id"))
                    .andExpect(cookie().value("user_id", not(emptyOrNullString())));
        }
    }

    @Nested
    class GetUserDetails {
        @Test
        void getUserDetails_whenValidRequest_returnOK() throws Exception {

            String expectedJsonResponse = """
                        {
                            "firstName": "John",
                            "lastName": "Doe",
                            "isGuest": true
                        }
                    """;


            mockMvc.perform(MockMvcRequestBuilders.get("/v1/users")
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        @Test
        void getUserDetails_whenUserNotFound_return404() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/v1/users")
                            .cookie(nonExistingUserAccessTokenCookie)
                            .cookie(nonExistingUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String reason = result.getResponse().getErrorMessage();
                        assertNotNull(reason);
                        assertTrue(reason.contains("User not found"), "Reason should contain 'User not found'");
                    });
        }

    }


    @Nested
    class UpdateUserDetails {
        String newFirstName = "Jack";
        String newLastName = "Sparrow";
        String newCurrency = "USD";

        UpdateUserDetailsRequest request =
                new UpdateUserDetailsRequest(newFirstName, newLastName, newCurrency);

        @Test
        void updateGuestUserDetails_whenValidRequest_returnOK() throws Exception {
            String expectedJsonResponse = """
                        {
                            "firstName": "%s",
                            "lastName": "%s",
                            "isGuest": true,
                            "currency": "%s"
                        }
                    """.formatted(newFirstName, newLastName, newCurrency);

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        @Test
        void updateGoogleUserDetails_whenValidRequest_returnOK() throws Exception {
            UpdateUserDetailsRequest requestNewName =
                    new UpdateUserDetailsRequest(newFirstName, "", "");

            String expectedJsonResponse = """
                        {
                            "firstName": "%s",
                            "lastName": "Smith",
                            "isGuest": false
                        }
                    """.formatted(newFirstName);

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(googleUserAccessTokenCookie)
                            .cookie(googleUserIdCookie)
                            .content(objectMapper.writeValueAsString(requestNewName)))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        @Test
        void updateUserDetails_whenUserNotFound_return404() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.put("/v1/users")
                            .cookie(nonExistingUserAccessTokenCookie)
                            .cookie(nonExistingUserIdCookie)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String reason = result.getResponse().getErrorMessage();
                        assertNotNull(reason);
                        assertTrue(reason.contains("User not found"), "Reason should contain 'User not found'");
                    });
        }

        @Test
        void updateUserDetails_whenFieldsAreEmpty_returnUnchangedData() throws Exception {
            String expectedJsonResponse = """
                        {
                            "firstName": "John",
                            "lastName": "Doe",
                            "isGuest": true
                        }
                    """;

            UpdateUserDetailsRequest emptyRequest =
                    new UpdateUserDetailsRequest("", "", "");

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .content(objectMapper.writeValueAsString(emptyRequest)))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }
    }

    @Nested
    class DeleteUser {
        @Test
        void deleteUser_whenValidRequest_returnOK() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie))
                    .andExpect(status().isNoContent())
                    .andExpect(cookie().exists("access_token"))
                    .andExpect(cookie().value("access_token", ""))
                    .andExpect(cookie().exists("user_id"))
                    .andExpect(cookie().value("user_id", ""));
        }

        @Test
        void deleteUser_whenUserNotFound_return404() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(nonExistingUserAccessTokenCookie)
                            .cookie(nonExistingUserIdCookie))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String reason = result.getResponse().getErrorMessage();
                        assertNotNull(reason);
                        assertTrue(reason.contains("User not found"), "Reason should contain 'User not found'");
                    });
        }
    }
}
