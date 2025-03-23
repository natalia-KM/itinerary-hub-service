package com.ih.itinerary_hub_service.integration.trips;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ih.itinerary_hub_service.integration.BaseIntegrationTest;
import com.ih.itinerary_hub_service.trips.persistence.repository.TripsRepository;
import com.ih.itinerary_hub_service.trips.requests.CreateTripRequest;
import com.ih.itinerary_hub_service.trips.requests.UpdateTripRequest;
import com.ih.itinerary_hub_service.users.persistence.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class TripsControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripsRepository tripsRepository;

    private static final UUID GUEST_USER_TRIP_ONE = UUID.fromString("9c5bb970-faef-419b-a447-365b9471a4b0");
    private static final UUID GUEST_USER_TRIP_TWO = UUID.fromString("cca715f0-8092-4208-80d3-afb7ef35d7f7");

    private static final LocalDateTime parisTripCreatedAt = LocalDateTime.of(2025, 3, 22, 0, 0, 0);
    private static final LocalDateTime parisTripStartDate = LocalDateTime.of(2025, 4, 25, 0, 0, 0);
    private static final LocalDateTime parisTripEndDate = LocalDateTime.of(2025, 4, 28, 0, 0, 0);
    private static final LocalDateTime londonTripCreatedAt = LocalDateTime.of(2022, 5, 12, 0, 0, 0);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Nested
    class CreateTrip {
        @Test
        void createTrip_whenValidRequest_returnStatusCreated() throws Exception {
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusDays(1);

            CreateTripRequest request = new CreateTripRequest(
                    "Trip name",
                    Optional.of(startDate),
                    Optional.of(endDate),
                    Optional.of("default")
            );

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/trips")
                        .cookie(guestUserAccessTokenCookie)
                        .cookie(guestUserIdCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    class GetAllTrips {
        @Test
        void getAllTrips_whenValidRequest_returnListOfTrips() throws Exception {
            String expectedJsonResponse = getGuestUserTripsExpectedReponse();

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips")
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        private static String getGuestUserTripsExpectedReponse() {

            return """
                    [
                        {
                            "tripId": "%s",
                            "tripName": "Paris Trip",
                            "createdAt": "%s",
                            "imageRef": "default",
                            "startDate": "%s",
                            "endDate": "%s"
                        },
                        {
                            "tripId": "%s",
                            "tripName": "London Trip",
                            "createdAt": "%s",
                            "imageRef": "default",
                            "startDate": null,
                            "endDate": null
                        }
                    ]
                    """.formatted(
                        GUEST_USER_TRIP_ONE,
                        parisTripCreatedAt.format(formatter),
                        parisTripStartDate.format(formatter),
                        parisTripEndDate.format(formatter),
                        GUEST_USER_TRIP_TWO,
                        londonTripCreatedAt.format(formatter)
                );
        }

        @Test
        void getAllTrips_whenNoTripsInAccount_returnEmptyList() throws Exception {
            String expectedJsonResponse = """
                        []
                    """;

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips")
                            .cookie(googleUserAccessTokenCookie)
                            .cookie(googleUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }
    }

    @Nested
    class GetTripById {
        @Test
        void getTripById_whenValidRequest_returnTrip() throws Exception {
            String expectedJsonResponse = """
                        {
                            "tripId": "%s",
                            "tripName": "Paris Trip",
                            "createdAt": "%s",
                            "imageRef": "default",
                            "startDate": "%s",
                            "endDate": "%s"
                        }
                    """.formatted(
                            GUEST_USER_TRIP_ONE,
                            parisTripCreatedAt.format(formatter),
                            parisTripStartDate.format(formatter),
                            parisTripEndDate.format(formatter)
                        );


            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}", GUEST_USER_TRIP_ONE.toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        @Test
        void getTripById_whenTripDoesNotExist_returnNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}", UUID.randomUUID().toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class UpdateTrip {
        @Test
        void updateTrip_whenValidRequest_updateTrip() throws Exception {
            LocalDateTime newStartDate = LocalDateTime.of(2024, 5, 12, 0, 0, 0);
            String newTripName = "Dubai Trip";
            String newTripImageRef = "image-1";

            UpdateTripRequest request = new UpdateTripRequest(
                    Optional.of(newTripName),
                    Optional.of(newStartDate),
                    Optional.empty(),
                    Optional.of(newTripImageRef)
            );

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/trips/{tripId}", GUEST_USER_TRIP_ONE.toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            String expectedJsonResponse = """
                        {
                            "tripId": "%s",
                            "tripName": "%s",
                            "createdAt": "%s",
                            "imageRef": "%s",
                            "startDate": "%s",
                            "endDate": "%s"
                        }
                    """.formatted(
                    GUEST_USER_TRIP_ONE,
                    newTripName,
                    parisTripCreatedAt.format(formatter),
                    newTripImageRef,
                    newStartDate.format(formatter),
                    parisTripEndDate.format(formatter)
            );

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}", GUEST_USER_TRIP_ONE.toString())
                        .cookie(guestUserAccessTokenCookie)
                        .cookie(guestUserIdCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        @Test
        void updateTrip_whenEmptyValues_thenIgnore() throws Exception {
            LocalDateTime newStartDate = LocalDateTime.of(2024, 5, 12, 0, 0, 0);

            UpdateTripRequest request = new UpdateTripRequest(
                    Optional.of(""),
                    Optional.of(newStartDate),
                    Optional.empty(),
                    Optional.of("")
            );

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/trips/{tripId}", GUEST_USER_TRIP_ONE.toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            String expectedJsonResponse = """
                        {
                            "tripId": "%s",
                            "tripName": "Paris Trip",
                            "createdAt": "%s",
                            "imageRef": "default",
                            "startDate": "%s",
                            "endDate": "%s"
                        }
                    """.formatted(
                    GUEST_USER_TRIP_ONE,
                    parisTripCreatedAt.format(formatter),
                    newStartDate.format(formatter),
                    parisTripEndDate.format(formatter)
            );

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}", GUEST_USER_TRIP_ONE.toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        @Test
        void updateTrip_whenTripDoesNotExist_returnNotFound() throws Exception {
            UpdateTripRequest request = new UpdateTripRequest(
                    Optional.of("name"),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of("imageRef")
            );

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/trips/{trpId}", UUID.randomUUID().toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteTrip {

        @Test
        void deleteTrip_whenValidRequest_deleteTrip() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/v1/trips/{tripId}", GUEST_USER_TRIP_ONE.toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}", GUEST_USER_TRIP_ONE.toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

        }
    }

    @Nested
    class Auth {
        @Test
        void createUser_shouldBeProtected() throws Exception {
            CreateTripRequest request = new CreateTripRequest(
                    "Trip name",
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of("default")
            );

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/trips")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void getTrips_shouldBeProtected() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }
    }
}