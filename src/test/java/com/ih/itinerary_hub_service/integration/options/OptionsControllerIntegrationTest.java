package com.ih.itinerary_hub_service.integration.options;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ih.itinerary_hub_service.integration.BaseIntegrationTest;
import com.ih.itinerary_hub_service.options.persistence.repository.OptionsRepository;
import com.ih.itinerary_hub_service.options.requests.CreateOptionRequest;
import com.ih.itinerary_hub_service.options.requests.UpdateOptionRequest;
import com.ih.itinerary_hub_service.sections.persistence.repository.SectionsRepository;
import com.ih.itinerary_hub_service.trips.persistence.repository.TripsRepository;
import com.ih.itinerary_hub_service.users.persistence.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OptionsControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripsRepository tripsRepository;

    @Autowired
    private SectionsRepository sectionsRepository;

    @Autowired
    private OptionsRepository optionsRepository;

    private static final String OPTION_ID = "0d78ebf0-0159-4843-b54b-a696644f26fc";
    private static final String OPTION_NAME = "Option 1";
    private static final Integer OPTION_ORDER = 1;

    @Nested
    class CreateOption {
        private final CreateOptionRequest createOptionRequest = new CreateOptionRequest(
                "Option Name", 4
        );

        @Test
        void createOption_whenValidRequest_thenCreateOption() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/v1/trips/{tripId}/sections/{sectionId}/options", GUEST_USER_TRIP_ONE.toString(), SECTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createOptionRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.optionName").value("Option Name"))
                    .andExpect(jsonPath("$.order").value(4))
                    .andExpect(jsonPath("$.optionId").exists());
        }

        @Test
        void createOption_withNullOrder_return400() throws Exception {
            CreateOptionRequest request = new CreateOptionRequest(
                    "Option Name", null
            );

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/trips/{tripId}/sections/{sectionId}/options", GUEST_USER_TRIP_ONE.toString(), SECTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void createOption_withEmptyName_return400() throws Exception {
            CreateOptionRequest request = new CreateOptionRequest(
                    "", 2
            );
            mockMvc.perform(MockMvcRequestBuilders.post("/v1/trips/{tripId}/sections/{sectionId}/options", GUEST_USER_TRIP_ONE.toString(), SECTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetOption {

        @Test
        void getOption_whenValidRequest_thenReturnOption() throws Exception {
            String expectedJsonResponse = """
                        {
                            "optionId": "%s",
                            "optionName": "%s",
                            "order": %s
                        }
                    """.formatted(
                    OPTION_ID,
                    OPTION_NAME,
                    OPTION_ORDER
            );

            mockMvc.perform(MockMvcRequestBuilders.get(
                    "/v1/sections/{sectionId}/options/{optionId}",
                                            SECTION_ONE,
                                            OPTION_ID)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        @Test
        void getOption_whenOptionDoesNotExist_returnNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(
                                    "/v1/sections/{sectionId}/options/{optionId}",
                                    SECTION_ONE,
                                    UUID.randomUUID())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class UpdateOption {
        private final UpdateOptionRequest updateOptionFullRequest = new UpdateOptionRequest(
                Optional.of("New Option Name"), Optional.of(5)
        );

        private final UpdateOptionRequest updateOptionNameRequest = new UpdateOptionRequest(
                Optional.empty(), Optional.of(5)
        );

        @Test
        void updateOption_whenValidRequest_thenUpdateOption() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.put(
                                    "/v1/sections/{sectionId}/options/{optionId}",
                                    SECTION_ONE,
                                    OPTION_ID)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateOptionFullRequest)))
                    .andExpect(status().isNoContent());

            String expectedJsonResponse = """
                        {
                            "optionId": "%s",
                            "optionName": "New Option Name",
                            "order": 5
                        }
                    """.formatted(OPTION_ID);

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    "/v1/sections/{sectionId}/options/{optionId}",
                                    SECTION_ONE,
                                    OPTION_ID)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        @Test
        void updateOption_whenEmptyValues_thenIgnore() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.put(
                                    "/v1/sections/{sectionId}/options/{optionId}",
                                    SECTION_ONE,
                                    OPTION_ID)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateOptionNameRequest)))
                    .andExpect(status().isNoContent());

            String expectedJsonResponse = """
                        {
                            "optionId": "%s",
                            "optionName": "%s",
                            "order": 5
                        }
                    """.formatted(OPTION_ID, OPTION_NAME);

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    "/v1/sections/{sectionId}/options/{optionId}",
                                    SECTION_ONE,
                                    OPTION_ID)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        @Test
        void updateOption_whenBlankValue_thenIgnore() throws Exception {
            UpdateOptionRequest updateOptionRequest = new UpdateOptionRequest(
                    Optional.of(" "), Optional.of(5)
            );

            mockMvc.perform(MockMvcRequestBuilders.put(
                                    "/v1/sections/{sectionId}/options/{optionId}",
                                    SECTION_ONE,
                                    OPTION_ID)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateOptionRequest)))
                    .andExpect(status().isNoContent());

            String expectedJsonResponse = """
                        {
                            "optionId": "%s",
                            "optionName": "%s",
                            "order": 5
                        }
                    """.formatted(OPTION_ID, OPTION_NAME);

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    "/v1/sections/{sectionId}/options/{optionId}",
                                    SECTION_ONE,
                                    OPTION_ID)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }


        @Test
        void updateOption_whenOptionDoesNotExist_returnNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.put(
                                    "/v1/sections/{sectionId}/options/{optionId}",
                                    SECTION_ONE,
                                    UUID.randomUUID())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateOptionNameRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteOption {

        @Disabled("Disabled until bug #24 has been fixed")
        @Test
        void deleteOption_whenValidRequest_thenDeleteOption() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete(
                                    "/v1/sections/{sectionId}/options/{optionId}",
                                    SECTION_ONE,
                                    OPTION_ID)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    "/v1/sections/{sectionId}/options/{optionId}",
                                    SECTION_ONE,
                                    OPTION_ID)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }
}