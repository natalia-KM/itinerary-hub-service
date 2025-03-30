package com.ih.itinerary_hub_service.integration.sections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ih.itinerary_hub_service.integration.BaseIntegrationTest;
import com.ih.itinerary_hub_service.options.persistence.repository.OptionsRepository;
import com.ih.itinerary_hub_service.sections.persistence.repository.SectionsRepository;
import com.ih.itinerary_hub_service.sections.requests.CreateSectionRequest;
import com.ih.itinerary_hub_service.sections.requests.UpdateSectionRequest;
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

class SectionsControllerIntegrationTest extends BaseIntegrationTest {
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

    private static final String SECTION_NAME = "Section 1";
    private static final Integer SECTION_ORDER = 1;

    @Nested
    class CreateSection {
        private final CreateSectionRequest createSectionRequest = new CreateSectionRequest(
                "Section Name", 5
        );

        @Test
        void createSection_whenValidRequest_thenCreateSection() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/v1/trips/{tripId}/sections", GUEST_USER_TRIP_ONE.toString())
                        .cookie(guestUserAccessTokenCookie)
                        .cookie(guestUserIdCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSectionRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.sectionName").value("Section Name"))
                    .andExpect(jsonPath("$.order").value(5))
                    .andExpect(jsonPath("$.sectionId").exists());
        }

        @Test
        void createSection_withNullOrder_return400() throws Exception {
            CreateSectionRequest request = new CreateSectionRequest(
                    "Name",
                    null
            );

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/trips/{tripId}/sections", GUEST_USER_TRIP_ONE.toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void createSection_withEmptyName_return400() throws Exception {
            CreateSectionRequest request = new CreateSectionRequest(
                    "",
                    5
            );

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/trips/{tripId}/sections", GUEST_USER_TRIP_ONE.toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetSection {

        @Test
        void getSection_whenValidRequest_thenGetSection() throws Exception {
            String expectedJsonResponse = """
                        {
                            "sectionId": "%s",
                            "sectionName": "%s",
                            "order": %s
                        }
                    """.formatted(
                        SECTION_ONE,
                            SECTION_NAME,
                            SECTION_ORDER
                        );

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}/sections/{sectionId}", GUEST_USER_TRIP_ONE.toString(), SECTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        @Test
        void getSection_whenSectionDoesNotExist_returnNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}/sections/{sectionId}", GUEST_USER_TRIP_ONE.toString(), UUID.randomUUID().toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class UpdateSection {
        private final UpdateSectionRequest updateSectionFullRequest = new UpdateSectionRequest(
                Optional.of("New Section Name"), Optional.of(5)
        );

        private final UpdateSectionRequest updateSectionNameRequest = new UpdateSectionRequest(
                Optional.of("New Section Name"), Optional.empty()
        );

        @Test
        void updateSection_whenValidRequest_thenUpdateSection() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.put("/v1/trips/{tripId}/sections/{sectionId}", GUEST_USER_TRIP_ONE.toString(), SECTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateSectionFullRequest)))
                    .andExpect(status().isNoContent());

            String expectedJsonResponse = """
                        {
                            "sectionId": "%s",
                            "sectionName": "New Section Name",
                            "order": 5
                        }
                    """.formatted(SECTION_ONE);

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}/sections/{sectionId}", GUEST_USER_TRIP_ONE.toString(), SECTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        @Test
        void updateSection_whenEmptyValues_thenIgnore() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.put("/v1/trips/{tripId}/sections/{sectionId}", GUEST_USER_TRIP_ONE.toString(), SECTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateSectionNameRequest)))
                    .andExpect(status().isNoContent());

            String expectedJsonResponse = """
                        {
                            "sectionId": "%s",
                            "sectionName": "New Section Name",
                            "order": %s
                        }
                    """.formatted(SECTION_ONE, SECTION_ORDER);

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}/sections/{sectionId}", GUEST_USER_TRIP_ONE.toString(), SECTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        @Test
        void updateSection_whenSectionDoesNotExist_returnNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.put("/v1/trips/{tripId}/sections/{sectionId}", GUEST_USER_TRIP_ONE.toString(), UUID.randomUUID().toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateSectionNameRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteSection {

        @Disabled("Disabled until bug #24 has been fixed")
        @Test
        void deleteSection_whenValidRequest_thenDeleteSection() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/v1/trips/{tripId}/sections/{sectionId}", GUEST_USER_TRIP_ONE.toString(), SECTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}/sections/{sectionId}", GUEST_USER_TRIP_ONE.toString(), SECTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }
}