package com.ih.itinerary_hub_service.unit.trips;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ih.itinerary_hub_service.config.CookieMaker;
import com.ih.itinerary_hub_service.config.GlobalExceptionHandler;
import com.ih.itinerary_hub_service.trips.controller.TripsController;
import com.ih.itinerary_hub_service.trips.requests.CreateTripRequest;
import com.ih.itinerary_hub_service.trips.requests.UpdateTripRequest;
import com.ih.itinerary_hub_service.trips.service.TripsService;
import com.ih.itinerary_hub_service.users.auth.JwtService;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import({ GlobalExceptionHandler.class })
class TripsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TripsService tripsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CookieMaker cookieMaker;

    private final UUID uuidForUser = UUID.randomUUID();

    @Nested
    class CreateTrip {

        @Test
        void createTrip_invalidRequest_return400() throws Exception {
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusDays(1);

            CreateTripRequest request = new CreateTripRequest(
                    "",
                    Optional.of(startDate),
                    Optional.of(endDate),
                    Optional.of("default")
            );

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/trips")
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", uuidForUser)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(tripsService, times(0)).createTrip(uuidForUser, request);
        }
        @Test
        void createTrip_dbFail_return500() throws Exception {
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusDays(1);

            CreateTripRequest request = new CreateTripRequest(
                    "Trip name",
                    Optional.of(startDate),
                    Optional.of(endDate),
                    Optional.of("default")
            );

            doThrow(new IllegalArgumentException("Custom exception")).when(tripsService).createTrip(uuidForUser, request);

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/trips")
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", uuidForUser)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(tripsService, times(1)).createTrip(uuidForUser, request);
        }
    }

    @Nested
    class UpdateTrip {

        @Test
        void updateTrip_dbFail_return500() throws Exception {
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusDays(1);
            UUID tripId = UUID.randomUUID();

            UpdateTripRequest request = new UpdateTripRequest(
                    Optional.of("Trip name"),
                    Optional.of(startDate),
                    Optional.of(endDate),
                    Optional.of("default")
            );

            doThrow(new IllegalArgumentException("Custom exception")).when(tripsService).updateTrip(uuidForUser, tripId, request);

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/trips/{tripId}", tripId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", uuidForUser)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(tripsService, times(1)).updateTrip(uuidForUser, tripId, request);
        }
    }

    @Nested
    class DeleteTrip {

        @Test
        void deleteTrip_dbFail_return500() throws Exception {
            UUID tripId = UUID.randomUUID();

            doThrow(new IllegalArgumentException("Custom exception")).when(tripsService).deleteTrip(uuidForUser, tripId);

            mockMvc.perform(MockMvcRequestBuilders.delete("/v1/trips/{tripId}", tripId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("userId", uuidForUser))
                    .andExpect(status().isInternalServerError());

            verify(tripsService, times(1)).deleteTrip(uuidForUser, tripId);
        }
    }
}