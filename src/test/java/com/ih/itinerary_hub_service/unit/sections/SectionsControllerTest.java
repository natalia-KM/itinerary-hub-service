package com.ih.itinerary_hub_service.unit.sections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ih.itinerary_hub_service.config.CookieMaker;
import com.ih.itinerary_hub_service.config.GlobalExceptionHandler;
import com.ih.itinerary_hub_service.sections.controller.SectionsController;
import com.ih.itinerary_hub_service.sections.requests.CreateSectionRequest;
import com.ih.itinerary_hub_service.sections.requests.UpdateSectionRequest;
import com.ih.itinerary_hub_service.sections.service.SectionService;
import com.ih.itinerary_hub_service.trips.service.TripsService;
import com.ih.itinerary_hub_service.users.auth.JwtService;
import com.ih.itinerary_hub_service.users.service.UserService;
import com.ih.itinerary_hub_service.utils.MockData;
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

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SectionsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import({ GlobalExceptionHandler.class })
class SectionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TripsService tripsService;

    @MockitoBean
    private SectionService sectionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CookieMaker cookieMaker;

    private final UUID userID = UUID.randomUUID();
    private final UUID tripID = UUID.randomUUID();
    private final UUID sectionID = UUID.randomUUID();

    @Nested
    class CreateSection {

        @Test
        void createSection_dbFail_return500() throws Exception {
            CreateSectionRequest request = new CreateSectionRequest(
                    "Section Name",
                    5
            );

            when(tripsService.getTrip(MockData.userId, MockData.tripId)).thenReturn(MockData.mockTrip);
            doThrow(new IllegalArgumentException("Custom exception")).when(sectionService).createSection(MockData.mockTrip, request);

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/trips/{tripId}/sections", MockData.tripId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", MockData.userId)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(sectionService, times(1)).createSection(MockData.mockTrip, request);
        }
    }

    @Nested
    class UpdateSection {

        @Test
        void updateSection_dbFail_return500() throws Exception {
            UpdateSectionRequest request = new UpdateSectionRequest(
                    Optional.of("Section Name"),
                    Optional.of(5)
            );

            doThrow(new IllegalArgumentException("Custom exception")).when(sectionService).updateSection(sectionID, tripID, request);

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/trips/{tripId}/sections/{sectionId}", tripID.toString(), sectionID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", userID)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(sectionService, times(1)).updateSection(sectionID, tripID, request);
        }
    }

    @Nested
    class DeleteSection {

        @Test
        void deleteSection_dbFail_return500() throws Exception {
            doThrow(new IllegalArgumentException("Custom exception")).when(sectionService).deleteSection(sectionID, tripID);

            mockMvc.perform(MockMvcRequestBuilders.delete("/v1/trips/{tripId}/sections/{sectionId}", tripID.toString(), sectionID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", userID))
                    .andExpect(status().isInternalServerError());

            verify(sectionService, times(1)).deleteSection(sectionID, tripID);
        }
    }

}