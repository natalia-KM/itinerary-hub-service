package com.ih.itinerary_hub_service.integration.trips;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ih.itinerary_hub_service.elements.persistence.repository.AccommodationElementRepository;
import com.ih.itinerary_hub_service.elements.persistence.repository.ActivityRepository;
import com.ih.itinerary_hub_service.elements.persistence.repository.BaseElementRepository;
import com.ih.itinerary_hub_service.elements.persistence.repository.TransportRepository;
import com.ih.itinerary_hub_service.integration.BaseIntegrationTest;
import com.ih.itinerary_hub_service.options.persistence.repository.OptionsRepository;
import com.ih.itinerary_hub_service.sections.persistence.repository.SectionsRepository;
import com.ih.itinerary_hub_service.trips.persistence.repository.TripsRepository;
import com.ih.itinerary_hub_service.trips.requests.CreateTripRequest;
import com.ih.itinerary_hub_service.trips.requests.UpdateTripRequest;
import com.ih.itinerary_hub_service.users.persistence.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Autowired
    private SectionsRepository sectionsRepository;

    @Autowired
    private OptionsRepository optionsRepository;

    @Autowired
    private BaseElementRepository elementRepository;

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AccommodationElementRepository accommodationElementRepository;

    @Autowired
    private AccommodationElementRepository aecRepository;

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
            String expectedJsonResponse = getGuestUserTripsExpectedResponse();

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips")
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }

        private static String getGuestUserTripsExpectedResponse() {

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
    class UpdateTrip {
        @Test
        void updateTrip_whenValidRequest_updateTrip() throws Exception {
            LocalDateTime startDate = LocalDateTime.of(2024, 5, 12, 0, 0, 0);
            String newTripName = "Dubai Trip";
            String newTripImageRef = "image-1";

            UpdateTripRequest request = new UpdateTripRequest(
                    Optional.of(newTripName),
                    Optional.of(startDate),
                    Optional.empty(),
                    Optional.of(newTripImageRef)
            );

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/trips/{tripId}", GUEST_USER_TRIP_ONE.toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}", GUEST_USER_TRIP_ONE.toString())
                        .cookie(guestUserAccessTokenCookie)
                        .cookie(guestUserIdCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tripDetails.tripId").value(GUEST_USER_TRIP_ONE.toString()))
                    .andExpect(jsonPath("$.tripDetails.tripName").value(newTripName))
                    .andExpect(jsonPath("$.tripDetails.createdAt").value(parisTripCreatedAt.format(formatter)))
                    .andExpect(jsonPath("$.tripDetails.imageRef").value(newTripImageRef))
                    .andExpect(jsonPath("$.tripDetails.startDate").value(startDate.format(formatter)))
                    .andExpect(jsonPath("$.tripDetails.endDate").value(parisTripEndDate.format(formatter)));
        }

        @Test
        void updateTrip_whenEmptyValues_thenIgnore() throws Exception {
            LocalDateTime newStartDate = LocalDateTime.of(2025, 8, 12, 0, 0, 0);

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
                    .andExpect(status().isNoContent());

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}", GUEST_USER_TRIP_ONE.toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tripDetails.tripId").value(GUEST_USER_TRIP_ONE.toString()))
                    .andExpect(jsonPath("$.tripDetails.tripName").value("Paris Trip"))
                    .andExpect(jsonPath("$.tripDetails.createdAt").value(parisTripCreatedAt.format(formatter)))
                    .andExpect(jsonPath("$.tripDetails.imageRef").value("default"))
                    .andExpect(jsonPath("$.tripDetails.startDate").value(newStartDate.format(formatter)))
                    .andExpect(jsonPath("$.tripDetails.endDate").value(parisTripEndDate.format(formatter)));
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

        @Disabled("Disabled until bug #24 has been fixed")
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

    @Nested
    class HappyPaths {
        @Test
        void getTripDetails() throws Exception {
            String expectedJsonResponse = """
                        {
                          "tripId": "9c5bb970-faef-419b-a447-365b9471a4b0",
                          "tripName": "Paris Trip",
                          "createdAt": "2025-03-22T00:00:00",
                          "imageRef": "default",
                          "startDate": "2025-04-25T00:00:00",
                          "endDate": "2025-04-28T00:00:00"
                        }
                      """;

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}/details", GUEST_USER_TRIP_ONE.toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse, JsonCompareMode.STRICT));
        }

        @Test
        void getTripById_whenValidRequest_returnTrip() throws Exception {
            String expectedJsonResponse = """
                     {
                          "tripDetails": {
                            "tripId": "9c5bb970-faef-419b-a447-365b9471a4b0",
                            "tripName": "Paris Trip",
                            "createdAt": "2025-03-22T00:00:00",
                            "imageRef": "default",
                            "startDate": "2025-04-25T00:00:00",
                            "endDate": "2025-04-28T00:00:00"
                          },
                          "sections": [
                            {
                              "sectionDetails": {
                                "sectionId": "a3c84e94-157b-436f-9e77-2b461c7c3bf2",
                                "sectionName": "Section 1",
                                "order": 1
                              },
                              "options": [
                                {
                                  "optionDetails": {
                                    "optionId": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                                    "optionName": "Option 1",
                                    "order": 1
                                  },
                                  "baseElementDetails": [
                                    {
                                      "baseElementID": "e4f56f0d-01ab-4ddb-be38-486ebefc4ede",
                                      "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                                      "lastUpdatedAt": "2022-05-15T00:00:00",
                                      "elementType": "ACCOMMODATION",
                                      "elementCategory": "Hotel",
                                      "link": "https://book-hotel.ih/",
                                      "price": null,
                                      "notes": null,
                                      "status": null,
                                      "order": 1,
                                      "place": "Hotel Name",
                                      "location": "Paris, Some Street",
                                      "accommodationType": "CHECK_IN",
                                      "dateTime": "2022-05-12T12:30:00"
                                    },
                                    {
                                      "baseElementID": "4e52ae05-06dc-423f-b86f-51a00cb8c452",
                                      "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                                      "lastUpdatedAt": "2022-05-12T00:00:00",
                                      "elementType": "TRANSPORT",
                                      "elementCategory": "Flight",
                                      "link": null,
                                      "price": 23.45,
                                      "notes": "Notes",
                                      "status": "PENDING",
                                      "order": 2,
                                      "originPlace": "London Heathrow",
                                      "destinationPlace": "Paris",
                                      "originDateTime": "2022-05-12T00:00:00",
                                      "destinationDateTime": "2022-05-15T12:00:00",
                                      "originProvider": null,
                                      "destinationProvider": "Ryanair"
                                    },
                                    {
                                      "baseElementID": "e4f56f0d-01ab-4ddb-be38-486ebefc4ede",
                                      "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                                      "lastUpdatedAt": "2022-05-15T00:00:00",
                                      "elementType": "ACCOMMODATION",
                                      "elementCategory": "Hotel",
                                      "link": "https://book-hotel.ih/",
                                      "price": null,
                                      "notes": null,
                                      "status": null,
                                      "order": 3,
                                      "place": "Hotel Name",
                                      "location": "Paris, Some Street",
                                      "accommodationType": "CHECK_OUT",
                                      "dateTime": "2022-05-13T14:00:00"
                                    }
                                  ]
                                },
                                {
                                  "optionDetails": {
                                    "optionId": "eb7fd861-6dba-4893-a4c8-bac1bd5a47ba",
                                    "optionName": "Option 2",
                                    "order": 2
                                  },
                                  "baseElementDetails": [
                                    {
                                      "baseElementID": "b647b387-31ad-4ffb-a9d2-91551d4b3138",
                                      "optionID": "eb7fd861-6dba-4893-a4c8-bac1bd5a47ba",
                                      "lastUpdatedAt": "2022-05-20T22:00:00",
                                      "elementType": "ACTIVITY",
                                      "elementCategory": "Restaurant",
                                      "link": null,
                                      "price": 1000,
                                      "notes": null,
                                      "status": "BOOKED",
                                      "order": 1,
                                      "activityName": "Escape Room",
                                      "location": "Paris, Street 2",
                                      "startsAt": "2022-05-15T13:00:00",
                                      "duration": 120
                                    }
                                  ]
                                }
                              ]
                            },
                            {
                              "sectionDetails": {
                                "sectionId": "c13dd7ad-8f7d-4f93-8edd-ee3951097592",
                                "sectionName": "Section 2",
                                "order": 2
                              },
                              "options": []
                            }
                          ]
                        }
                    """;

            mockMvc.perform(MockMvcRequestBuilders.get("/v1/trips/{tripId}", GUEST_USER_TRIP_ONE.toString())
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse, JsonCompareMode.STRICT));
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
}