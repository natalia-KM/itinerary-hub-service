package com.ih.itinerary_hub_service.unit.options;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ih.itinerary_hub_service.config.CookieMaker;
import com.ih.itinerary_hub_service.config.GlobalExceptionHandler;
import com.ih.itinerary_hub_service.options.controller.OptionsController;
import com.ih.itinerary_hub_service.options.requests.CreateOptionRequest;
import com.ih.itinerary_hub_service.options.requests.UpdateOptionRequest;
import com.ih.itinerary_hub_service.options.service.OptionsService;
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

@WebMvcTest(OptionsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import({ GlobalExceptionHandler.class })
class OptionsControllerTest {
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
    private OptionsService optionsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CookieMaker cookieMaker;

    private final UUID userID = UUID.randomUUID();
    private final UUID sectionID = UUID.randomUUID();
    private final UUID optionID = UUID.randomUUID();

    @Nested
    class CreateOption {

        @Test
        void createOption_dbFail_return500() throws Exception {
            CreateOptionRequest request = new CreateOptionRequest(
                    "Option Name", 4
            );
            when(sectionService.getSection(MockData.sectionId, MockData.tripId)).thenReturn(MockData.mockSection);
            doThrow(new IllegalArgumentException("Custom exception")).when(optionsService).createOption(MockData.mockSection, request);

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/trips/{tripId}/sections/{sectionId}/options", MockData.tripId, MockData.sectionId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", userID)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(optionsService, times(1)).createOption(MockData.mockSection, request);
        }
    }

    @Nested
    class UpdateOption {

        @Test
        void updateOption_dbFail_return500() throws Exception {
            UpdateOptionRequest request = new UpdateOptionRequest(
                    Optional.of("New Option Name"), Optional.of(5)
            );

            doThrow(new IllegalArgumentException("Custom exception")).when(optionsService).updateOption(optionID, sectionID, request);

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/sections/{sectionId}/options/{optionId}", sectionID.toString(), optionID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", userID)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(optionsService, times(1)).updateOption(optionID, sectionID, request);
        }
    }

    @Nested
    class DeleteOption {

        @Test
        void deleteOption_dbFail_return500() throws Exception {
            doThrow(new IllegalArgumentException("Custom exception")).when(optionsService).deleteOption(optionID, sectionID);

            mockMvc.perform(MockMvcRequestBuilders.delete("/v1/sections/{sectionId}/options/{optionId}", sectionID.toString(), optionID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", userID))
                    .andExpect(status().isInternalServerError());

            verify(optionsService, times(1)).deleteOption(optionID, sectionID);
        }
    }
}