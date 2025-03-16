package com.ih.itinerary_hub_service.unit.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ih.itinerary_hub_service.config.CookieMaker;
import com.ih.itinerary_hub_service.config.GlobalExceptionHandler;
import com.ih.itinerary_hub_service.users.auth.JwtService;
import com.ih.itinerary_hub_service.users.controller.UserController;
import com.ih.itinerary_hub_service.users.exceptions.UserNotFoundException;
import com.ih.itinerary_hub_service.users.persistence.entity.User;
import com.ih.itinerary_hub_service.users.requests.UpdateUserDetailsRequest;
import com.ih.itinerary_hub_service.users.requests.UserDetailsRequest;
import com.ih.itinerary_hub_service.users.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import({ GlobalExceptionHandler.class })
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CookieMaker cookieMaker;

    private final UUID uuidForUser = UUID.randomUUID();

    private final User mockUser = new User(
            uuidForUser,
            "John",
            "Doe",
            LocalDateTime.now(),
            true,
            null,
            null,
            null
    );

    @Nested
    class CreateUser {
        @Test
        void createUser_whenValidRequest_returnStatusCreated() throws Exception {
            when(userService.createGuestUser(anyString(), anyString())).thenReturn(mockUser);
            when(jwtService.generateAccessToken(anyString(), any())).thenReturn("someTokenValue");

            UserDetailsRequest request = new UserDetailsRequest("John", "Doe");

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/users/guest")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            verify(userService, times(1)).createGuestUser(anyString(), anyString());
        }

        @Test
        void createUser_whenDbThrowsError_return500() throws Exception {
            when(userService.createGuestUser(anyString(), anyString())).thenThrow(new IllegalArgumentException("Custom exception"));

            UserDetailsRequest request = new UserDetailsRequest("John", "Doe");

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/users/guest")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(MockMvcResultMatchers.content().string(containsString("Internal Server Error")));


            verify(userService, times(1)).createGuestUser(anyString(), anyString());
        }

        @Test
        void createUser_whenNoFirstName_return400() throws Exception {
            UserDetailsRequest request = new UserDetailsRequest("", "Doe");

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/users/guest")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(userService, times(0)).createGuestUser(anyString(), anyString());
        }

        @Test
        void createUser_whenLastName_return400() throws Exception {
            UserDetailsRequest request = new UserDetailsRequest("John", null);

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/users/guest")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(userService, times(0)).createGuestUser(anyString(), anyString());
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

            when(userService.getUserById(uuidForUser)).thenReturn(mockUser);

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", uuidForUser))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));

            verify(userService, times(1)).getUserById(uuidForUser);
        }

        @Test
        void getUserDetails_whenUserNotFound_return404() throws Exception {
            when(userService.getUserById(uuidForUser)).thenThrow(new UserNotFoundException("User not found"));

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", uuidForUser))
                    .andExpect(status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string("User not found"));

            verify(userService, times(1)).getUserById(uuidForUser);
        }
    }

    @Nested
    class UpdateUserDetails {
        String newFirstName = "Jack";
        String newLastName = "Sparrow";
        String newCurrency = "USD";

        UpdateUserDetailsRequest request =
                new UpdateUserDetailsRequest(newFirstName, newLastName, newCurrency);

        User updatedUser = new User(
                uuidForUser,
                newFirstName,
                newLastName,
                LocalDateTime.now(),
                true,
                null,
                newCurrency,
                null
        );

        @Test
        void updateUserDetails_whenValidRequest_returnOK() throws Exception {

            String expectedJsonResponse = """
                        {
                            "firstName": "%s",
                            "lastName": "%s",
                            "isGuest": true,
                            "currency": "%s"
                        }
                    """.formatted(newFirstName, newLastName, newCurrency);

            when(userService.updateUserDetails(uuidForUser, newFirstName, newLastName, newCurrency)).thenReturn(updatedUser);

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", uuidForUser)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));

            verify(userService, times(1)).updateUserDetails(uuidForUser, newFirstName, newLastName, newCurrency);
        }

        @Test
        void updateUserDetails_whenUserNotFound_return404() throws Exception {
            when(userService.updateUserDetails(uuidForUser, newFirstName, newLastName, newCurrency)).thenThrow(new UserNotFoundException("User not found"));

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", uuidForUser)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string("User not found"));

            verify(userService, times(1)).updateUserDetails(uuidForUser, newFirstName, newLastName, newCurrency);
        }

        @Test
        void updateUserDetails_whenDbFails_return500() throws Exception {
            when(userService.updateUserDetails(uuidForUser, newFirstName, newLastName, newCurrency)).thenThrow(new RuntimeException("Couldn't update user"));

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", uuidForUser)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(userService, times(1)).updateUserDetails(uuidForUser, newFirstName, newLastName, newCurrency);
        }
    }

    @Nested
    class DeleteUser {
        @Test
        void deleteUser_whenValidRequest_returnOK() throws Exception {
            doNothing().when(userService).deleteUser(uuidForUser);

            mockMvc.perform(MockMvcRequestBuilders.delete("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", uuidForUser))
                    .andExpect(status().isNoContent());

            verify(userService, times(1)).deleteUser(uuidForUser);
        }

        @Test
        void deleteUser_whenUserNotFound_return404() throws Exception {
            doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(uuidForUser);

            mockMvc.perform(MockMvcRequestBuilders.delete("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", uuidForUser))
                    .andExpect(status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string("User not found"));

            verify(userService, times(1)).deleteUser((uuidForUser));
        }
    }
}