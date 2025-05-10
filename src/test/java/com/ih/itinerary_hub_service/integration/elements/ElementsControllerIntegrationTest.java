package com.ih.itinerary_hub_service.integration.elements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ih.itinerary_hub_service.elements.requests.*;
import com.ih.itinerary_hub_service.elements.service.ElementsService;
import com.ih.itinerary_hub_service.elements.types.AccommodationType;
import com.ih.itinerary_hub_service.elements.types.ElementStatus;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.integration.BaseIntegrationTest;
import com.ih.itinerary_hub_service.options.persistence.repository.OptionsRepository;
import com.ih.itinerary_hub_service.passengers.persistence.repository.ElementPassengerRepository;
import com.ih.itinerary_hub_service.passengers.persistence.repository.PassengersRepository;
import com.ih.itinerary_hub_service.sections.persistence.repository.SectionsRepository;
import com.ih.itinerary_hub_service.trips.persistence.repository.TripsRepository;
import com.ih.itinerary_hub_service.users.persistence.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ElementsControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripsRepository tripsRepository;

    @Autowired
    private ElementsService elementsService;

    @Autowired
    private OptionsRepository optionsRepository;

    @Autowired
    private SectionsRepository sectionsRepository;

    @Autowired
    private PassengersRepository passengersRepository;

    @Autowired
    private ElementPassengerRepository elementPassengerRepository;

    private static final String BASE_ELEMENTS_URL = "/v1/sections/{sectionId}/options/{optionId}/elements";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final UUID PASSENGER_ONE_UUID = UUID.fromString(PASSENGER_ONE);
    private final UUID PASSENGER_TWO_UUID = UUID.fromString(PASSENGER_TWO);
    private final UUID PASSENGER_THREE_UUID = UUID.fromString(PASSENGER_THREE);
    private final UUID PASSENGER_FOUR_UUID = UUID.fromString(PASSENGER_FOUR);
    private final UUID PASSENGER_FIVE_UUID = UUID.fromString(PASSENGER_FIVE);

    @Nested
    class CreateElement {

        @Test
        void createTransportElement() throws Exception {
            String originPlace = "origin";
            String destinationPlace = "destination";
            LocalDateTime originTime = LocalDateTime.of(2025, 3, 22, 0, 0, 0);
            LocalDateTime destinationTime = LocalDateTime.of(2025, 4, 25, 0, 0, 0);
            Integer order = 1;

            BaseElementRequest base = new BaseElementRequest(
                    ElementType.TRANSPORT,
                    "Flight",
                    null,
                    BigDecimal.valueOf(23.45),
                    "Notes",
                    ElementStatus.PENDING,
                    List.of(PASSENGER_ONE_UUID, PASSENGER_FOUR_UUID)
            );


            TransportElementRequest request = TransportElementRequest.builder()
                    .baseElementRequest(base)
                    .originPlace(originPlace)
                    .destinationPlace(destinationPlace)
                    .originDateTime(originTime)
                    .destinationDateTime(destinationTime)
                    .order(order)
                    .build();

            String expectedResponse = """
                        {
                          "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                          "elementType": "TRANSPORT",
                          "elementCategory": "Flight",
                          "link": null,
                          "price": 23.45,
                          "notes": "Notes",
                          "status": "PENDING",
                          "order": 1,
                          "passengerDetailsList": [
                            {
                              "passengerId": "0e85075f-be86-4b31-96ec-08feea54fb0e",
                              "firstName": "John",
                              "lastName": "Doe",
                              "avatar": "dog"
                            },
                            {
                              "passengerId": "d2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f",
                              "firstName": "Clara",
                              "lastName": "Nguyen",
                              "avatar": "fox"
                            }
                          ],
                          "originPlace": "origin",
                          "destinationPlace": "destination",
                          "originDateTime": "2025-03-22T00:00:00",
                          "destinationDateTime": "2025-04-25T00:00:00",
                          "provider": null
                        }
                    """;

            mockMvc.perform(MockMvcRequestBuilders.post(
                                    BASE_ELEMENTS_URL + "/transport",
                                    SECTION_ONE,
                                    OPTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.baseElementID").exists())
                    .andExpect(jsonPath("$.lastUpdatedAt").exists())
                    .andExpect(content().json(expectedResponse));
        }

        @Test
        void createActivityElement() throws Exception {
            String link = "https://some-link.co";
            String activityName = "activity";
            String location = "location";
            LocalDateTime startsAt = LocalDateTime.of(2025, 3, 22, 0, 0, 0);
            Integer order = 3;

            BaseElementRequest base = new BaseElementRequest(
                    ElementType.ACTIVITY,
                    "Escape room",
                    link,
                    BigDecimal.valueOf(23.45),
                    "Notes",
                    ElementStatus.PENDING,
                    null
            );
            ActivityElementRequest request = ActivityElementRequest.builder()
                    .baseElementRequest(base)
                    .activityName(activityName)
                    .location(location)
                    .startsAt(startsAt)
                    .duration(null)
                    .order(order)
                    .build();

            String expectedResponse = """
                        {
                          "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                          "elementType": "ACTIVITY",
                          "elementCategory": "Escape room",
                          "link": "https://some-link.co",
                          "price": 23.45,
                          "notes": "Notes",
                          "status": "PENDING",
                          "order": 3,
                          "passengerDetailsList": [],
                          "activityName": "activity",
                          "location": "location",
                          "startsAt": "2025-03-22T00:00:00",
                          "duration": null
                        }
                    """;

            mockMvc.perform(MockMvcRequestBuilders.post(
                                    BASE_ELEMENTS_URL + "/activity",
                                    SECTION_ONE,
                                    OPTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.baseElementID").exists())
                    .andExpect(jsonPath("$.lastUpdatedAt").exists())
                    .andExpect(content().json(expectedResponse));
        }

        @Test
        void createAccommodationElement() throws Exception {
            String link = "https://some-link.co";
            String place = "place";
            String location = "location";
            LocalDateTime checkInTime = LocalDateTime.of(2025, 3, 22, 0, 0, 0);
            LocalDateTime checkOutTime = checkInTime.plusDays(1);

            BaseElementRequest base = new BaseElementRequest(
                    ElementType.ACCOMMODATION,
                    "AirBnb",
                    link,
                    null,
                    null,
                    null,
                    List.of(PASSENGER_FIVE_UUID)
            );

            AccommodationEventRequest checkIn = new AccommodationEventRequest(checkInTime, 1);
            AccommodationEventRequest checkOut = new AccommodationEventRequest(checkOutTime, 3);

            AccommodationElementRequest request = AccommodationElementRequest.builder()
                    .baseElementRequest(base)
                    .place(place)
                    .location(location)
                    .checkIn(checkIn)
                    .checkOut(checkOut)
                    .build();

            String expectedResponse = """
                        [
                          {
                            "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                            "elementType": "ACCOMMODATION",
                            "elementCategory": "AirBnb",
                            "link": "https://some-link.co",
                            "price": null,
                            "notes": null,
                            "status": null,
                            "order": 1,
                            "passengerDetailsList": [
                              {
                                "passengerId": "ba9d1df2-99b1-4df4-ae00-c5d9ef6e5f57",
                                "firstName": "Ethan",
                                "lastName": "Brown",
                                "avatar": "turtle"
                              }
                            ],
                            "place": "place",
                            "location": "location",
                            "accommodationType": "CHECK_IN",
                            "dateTime": "2025-03-22T00:00:00"
                          },
                          {
                            "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                            "elementType": "ACCOMMODATION",
                            "elementCategory": "AirBnb",
                            "link": "https://some-link.co",
                            "price": null,
                            "notes": null,
                            "status": null,
                            "order": 3,
                            "passengerDetailsList": [
                              {
                                "passengerId": "ba9d1df2-99b1-4df4-ae00-c5d9ef6e5f57",
                                "firstName": "Ethan",
                                "lastName": "Brown",
                                "avatar": "turtle"
                              }
                            ],
                            "place": "place",
                            "location": "location",
                            "accommodationType": "CHECK_OUT",
                            "dateTime": "2025-03-23T00:00:00"
                          }
                        ]
                    """;

            mockMvc.perform(MockMvcRequestBuilders.post(
                                    BASE_ELEMENTS_URL + "/accommodation",
                                    SECTION_ONE,
                                    OPTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$[0].baseElementID").exists())
                    .andExpect(jsonPath("$[0].lastUpdatedAt").exists())
                    .andExpect(jsonPath("$[1].baseElementID").exists())
                    .andExpect(jsonPath("$[1].lastUpdatedAt").exists())
                    .andExpect(content().json(expectedResponse, JsonCompareMode.LENIENT));
        }
    }

    @Nested
    class GetElement {

        @Test
        void shouldGetTransportElement() throws Exception {
            String expectedResponse = """
                        {
                          "baseElementID": "4e52ae05-06dc-423f-b86f-51a00cb8c452",
                          "elementID": "674a2a9c-2dc5-4d00-a9ee-e4f051a17194",
                          "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                          "lastUpdatedAt": "2022-05-12T00:00:00",
                          "elementType": "TRANSPORT",
                          "elementCategory": "Flight",
                          "link": null,
                          "price": 23.45,
                          "notes": "Notes",
                          "status": "PENDING",
                          "order": 2,
                          "passengerDetailsList": [
                            {
                              "passengerId": "0e85075f-be86-4b31-96ec-08feea54fb0e",
                              "firstName": "John",
                              "lastName": "Doe",
                              "avatar": "dog"
                            },
                            {
                              "passengerId": "3c2c02d3-8a7f-4a1c-94bb-4cce3e9b90c1",
                              "firstName": "Alice",
                              "lastName": "Smith",
                              "avatar": "cat"
                            },
                            {
                              "passengerId": "e0a5409f-6b9e-4f2c-8418-a9275aa4ae52",
                              "firstName": "Bob",
                              "lastName": "Johnson",
                              "avatar": "parrot"
                            },
                            {
                              "passengerId": "d2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f",
                              "firstName": "Clara",
                              "lastName": "Nguyen",
                              "avatar": "fox"
                            }
                          ],
                          "originPlace": "London Heathrow",
                          "destinationPlace": "Paris",
                          "originDateTime": "2022-05-12T00:00:00",
                          "destinationDateTime": "2022-05-15T12:00:00",
                          "provider": null
                        }
                    """;

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/transport",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    TRANSPORT_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponse, JsonCompareMode.STRICT));
        }

        @Test
        void shouldGetActivityElement() throws Exception {
            String expectedResponse = """
                       {
                         "baseElementID": "b647b387-31ad-4ffb-a9d2-91551d4b3138",
                         "elementID": "82c24ee6-075f-4d8c-913d-1d06f325fd43",
                         "optionID": "eb7fd861-6dba-4893-a4c8-bac1bd5a47ba",
                         "lastUpdatedAt": "2022-05-20T22:00:00",
                         "elementType": "ACTIVITY",
                         "elementCategory": "Restaurant",
                         "link": null,
                         "price": 1000,
                         "notes": null,
                         "status": "BOOKED",
                         "order": 1,
                         "passengerDetailsList": [],
                         "activityName": "Escape Room",
                         "location": "Paris, Street 2",
                         "startsAt": "2022-05-15T13:00:00",
                         "duration": 120
                       }
                    """;

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/activity",
                                    SECTION_ONE,
                                    OPTION_TWO,
                                    ACTIVITY_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponse, JsonCompareMode.STRICT));
        }

        @Test
        void shouldGetAccommElement() throws Exception {
            String expectedResponse = """
                       {
                         "baseElementID": "e4f56f0d-01ab-4ddb-be38-486ebefc4ede",
                         "elementID": "0347f675-1040-461c-b3ec-af80a0910850",
                         "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                         "lastUpdatedAt": "2022-05-15T00:00:00",
                         "elementType": "ACCOMMODATION",
                         "elementCategory": "Hotel",
                         "link": "https://book-hotel.ih/",
                         "price": null,
                         "notes": null,
                         "status": null,
                         "order": 1,
                         "passengerDetailsList": [
                           {
                             "passengerId": "d2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f",
                             "firstName": "Clara",
                             "lastName": "Nguyen",
                             "avatar": "fox"
                           },
                           {
                             "passengerId": "ba9d1df2-99b1-4df4-ae00-c5d9ef6e5f57",
                             "firstName": "Ethan",
                             "lastName": "Brown",
                             "avatar": "turtle"
                           }
                         ],
                         "place": "Hotel Name",
                         "location": "Paris, Some Street",
                         "accommodationType": "CHECK_IN",
                         "dateTime": "2022-05-12T12:30:00"
                       }
                    """;

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/accommodation?type=CHECK_IN",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    ACCOMMODATION_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponse, JsonCompareMode.STRICT));
        }

        @Test
        void shouldThrow_whenAccommodationTypeIsNotPassed() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/accommodation",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    ACCOMMODATION_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("Required parameter 'type' is not present."));
        }

        @Test
        void shouldThrow_whenAccommodationTypeIsInvalid() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/accommodation?type=CHECK",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    ACCOMMODATION_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid accommodation type: CHECK"));
        }

        @Test
        void shouldThrow_whenOptionDoesNotExist() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/transport",
                                    SECTION_ONE,
                                    UUID.randomUUID(),
                                    TRANSPORT_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Option with not found"));
        }

        @Test
        void shouldThrow_whenTransportElementDoesNotExist() throws Exception {
            UUID randomId = UUID.randomUUID();

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/transport",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    randomId)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Could not find baseElement with id " + randomId));
        }

        @Test
        void shouldThrow_whenActivityElementDoesNotExist() throws Exception {
            UUID randomId = UUID.randomUUID();
            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/activity",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    randomId)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Could not find baseElement with id " + randomId));
        }

        @Test
        void shouldThrow_whenAccommElementDoesNotExist() throws Exception {
            UUID randomId = UUID.randomUUID();
            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/accommodation?type=CHECK_IN",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    randomId)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Could not find baseElement with id " + randomId));
        }
    }

    @Nested
    class GetElements {
        @Test
        void shouldGetAllElementsInAnOption() throws Exception {
            String expectedResponse = """
                    [
                      {
                        "baseElementID": "e4f56f0d-01ab-4ddb-be38-486ebefc4ede",
                        "elementID": "0347f675-1040-461c-b3ec-af80a0910850",
                        "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                        "lastUpdatedAt": "2022-05-15T00:00:00",
                        "elementType": "ACCOMMODATION",
                        "elementCategory": "Hotel",
                        "link": "https://book-hotel.ih/",
                        "price": null,
                        "notes": null,
                        "status": null,
                        "order": 1,
                        "passengerDetailsList": [
                          {
                            "passengerId": "d2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f",
                            "firstName": "Clara",
                            "lastName": "Nguyen",
                            "avatar": "fox"
                          },
                          {
                            "passengerId": "ba9d1df2-99b1-4df4-ae00-c5d9ef6e5f57",
                            "firstName": "Ethan",
                            "lastName": "Brown",
                            "avatar": "turtle"
                          }
                        ],
                        "place": "Hotel Name",
                        "location": "Paris, Some Street",
                        "accommodationType": "CHECK_IN",
                        "dateTime": "2022-05-12T12:30:00"
                      },
                      {
                        "baseElementID": "4e52ae05-06dc-423f-b86f-51a00cb8c452",
                        "elementID": "674a2a9c-2dc5-4d00-a9ee-e4f051a17194",
                        "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                        "lastUpdatedAt": "2022-05-12T00:00:00",
                        "elementType": "TRANSPORT",
                        "elementCategory": "Flight",
                        "link": null,
                        "price": 23.45,
                        "notes": "Notes",
                        "status": "PENDING",
                        "order": 2,
                        "passengerDetailsList": [
                          {
                            "passengerId": "0e85075f-be86-4b31-96ec-08feea54fb0e",
                            "firstName": "John",
                            "lastName": "Doe",
                            "avatar": "dog"
                          },
                          {
                            "passengerId": "3c2c02d3-8a7f-4a1c-94bb-4cce3e9b90c1",
                            "firstName": "Alice",
                            "lastName": "Smith",
                            "avatar": "cat"
                          },
                          {
                            "passengerId": "e0a5409f-6b9e-4f2c-8418-a9275aa4ae52",
                            "firstName": "Bob",
                            "lastName": "Johnson",
                            "avatar": "parrot"
                          },
                          {
                            "passengerId": "d2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f",
                            "firstName": "Clara",
                            "lastName": "Nguyen",
                            "avatar": "fox"
                          }
                        ],
                        "originPlace": "London Heathrow",
                        "destinationPlace": "Paris",
                        "originDateTime": "2022-05-12T00:00:00",
                        "destinationDateTime": "2022-05-15T12:00:00",
                        "provider": null
                      },
                      {
                        "baseElementID": "e4f56f0d-01ab-4ddb-be38-486ebefc4ede",
                        "elementID": "f8876598-c138-4bd1-8055-5294bda159be",
                        "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                        "lastUpdatedAt": "2022-05-15T00:00:00",
                        "elementType": "ACCOMMODATION",
                        "elementCategory": "Hotel",
                        "link": "https://book-hotel.ih/",
                        "price": null,
                        "notes": null,
                        "status": null,
                        "order": 3,
                        "passengerDetailsList": [
                          {
                            "passengerId": "d2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f",
                            "firstName": "Clara",
                            "lastName": "Nguyen",
                            "avatar": "fox"
                          },
                          {
                            "passengerId": "ba9d1df2-99b1-4df4-ae00-c5d9ef6e5f57",
                            "firstName": "Ethan",
                            "lastName": "Brown",
                            "avatar": "turtle"
                          }
                        ],
                        "place": "Hotel Name",
                        "location": "Paris, Some Street",
                        "accommodationType": "CHECK_OUT",
                        "dateTime": "2022-05-13T14:00:00"
                      }
                    ]
                    """;

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    "/v1/options/{optionId}/elements",
                                    OPTION_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponse, JsonCompareMode.STRICT));
        }
    }

    @Nested
    class UpdateElement {

        @Test
        void shouldUpdateTransportElement() throws Exception {
            String newLink = "https://new-link.co";
            BigDecimal newPrice = BigDecimal.valueOf(53.45);
            String newOriginPlace = "Madrid";
            LocalDateTime newDestinationTime = LocalDateTime.of(2023, 4, 25, 0, 0, 0);
            String newProvider = "Ryanair";

            LocalDateTime currentDateTime = LocalDateTime.now();

            BaseElementRequest base = BaseElementRequest.builder()
                    .link(newLink)
                    .price(newPrice)
                    .elementCategory("Train")
                    .build();

            TransportElementRequest request = TransportElementRequest.builder()
                    .baseElementRequest(base)
                    .originPlace(newOriginPlace)
                    .destinationDateTime(newDestinationTime)
                    .provider(newProvider)
                    .build();

            String expectedResponse = """
                      {
                        "baseElementID": "4e52ae05-06dc-423f-b86f-51a00cb8c452",
                        "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                        "elementType": "TRANSPORT",
                        "elementCategory": "Train",
                        "link": "%s",
                        "price": %s,
                        "notes": "Notes",
                        "status": "PENDING",
                        "order": 2,
                        "passengerDetailsList": [
                          {
                            "passengerId": "0e85075f-be86-4b31-96ec-08feea54fb0e",
                            "firstName": "John",
                            "lastName": "Doe",
                            "avatar": "dog"
                          },
                          {
                            "passengerId": "3c2c02d3-8a7f-4a1c-94bb-4cce3e9b90c1",
                            "firstName": "Alice",
                            "lastName": "Smith",
                            "avatar": "cat"
                          },
                          {
                            "passengerId": "e0a5409f-6b9e-4f2c-8418-a9275aa4ae52",
                            "firstName": "Bob",
                            "lastName": "Johnson",
                            "avatar": "parrot"
                          },
                          {
                            "passengerId": "d2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f",
                            "firstName": "Clara",
                            "lastName": "Nguyen",
                            "avatar": "fox"
                          }
                        ],
                        "originPlace": "%s",
                        "destinationPlace": "Paris",
                        "originDateTime": "2022-05-12T00:00:00",
                        "destinationDateTime": "%s",
                        "provider": "%s"
                      }
                    """.formatted(
                    newLink,
                    newPrice,
                    newOriginPlace,
                    newDestinationTime.format(formatter),
                    newProvider
            );

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/transport",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    TRANSPORT_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.lastUpdatedAt").exists())
                    .andExpect(content().json(expectedResponse, JsonCompareMode.LENIENT))
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();

            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            String lastUpdatedAt = jsonResponse.get("lastUpdatedAt").asText();

            LocalDateTime updatedTime = LocalDateTime.parse(lastUpdatedAt);

            assertThat(updatedTime).isBetween(currentDateTime.minusSeconds(2), currentDateTime.plusSeconds(2));
        }

        @Test
        void shouldUpdateActivityElement() throws Exception {
            String newNotes = " ";
            ElementStatus newStatus = ElementStatus.CANCELLED;
            String newActivityName = "Axe Throwing";
            LocalDateTime newStartTime = LocalDateTime.of(2023, 4, 25, 0, 0, 0);
            Integer newDuration = 60;

            LocalDateTime currentDateTime = LocalDateTime.now();

            BaseElementRequest base = BaseElementRequest.builder()
                    .notes(newNotes)
                    .status(newStatus)
                    .build();

            ActivityElementRequest request = ActivityElementRequest.builder()
                    .baseElementRequest(base)
                    .activityName(newActivityName)
                    .startsAt(newStartTime)
                    .duration(newDuration)
                    .build();

            String expectedResponse = """
                      {
                        "elementType": "ACTIVITY",
                        "elementCategory": "Restaurant",
                        "link": null,
                        "price": 1000,
                        "notes": null,
                        "status": "%s",
                        "order": 1,
                        "passengerDetailsList": [],
                        "activityName": "%s",
                        "location": "Paris, Street 2",
                        "startsAt": "%s",
                        "duration": %s
                      }
                    """.formatted(
                    newStatus,
                    newActivityName,
                    newStartTime.format(formatter),
                    newDuration
            );

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/activity",
                                    SECTION_ONE,
                                    OPTION_TWO,
                                    ACTIVITY_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.lastUpdatedAt").exists())
                    .andExpect(content().json(expectedResponse, JsonCompareMode.LENIENT))
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();

            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            String lastUpdatedAt = jsonResponse.get("lastUpdatedAt").asText();

            LocalDateTime updatedTime = LocalDateTime.parse(lastUpdatedAt);

            assertThat(updatedTime).isBetween(currentDateTime.minusSeconds(2), currentDateTime.plusSeconds(2));
        }

        @Test
        void shouldUpdateAccommodationElement() throws Exception {
            BigDecimal newPrice = BigDecimal.valueOf(100.50);
            String newNotes = "Updated notes";
            String newLocation = " ";
            LocalDateTime newCheckInTime = LocalDateTime.of(2023, 4, 25, 0, 0, 0);
            Integer newCheckOutOrder = 2;

            LocalDateTime currentDateTime = LocalDateTime.now();

            BaseElementRequest base = BaseElementRequest.builder()
                    .notes(newNotes)
                    .price(newPrice)
                    .build();

            AccommodationEventRequest checkIn = new AccommodationEventRequest(newCheckInTime, 1);
            AccommodationEventRequest checkOut = new AccommodationEventRequest(null, newCheckOutOrder);

            AccommodationElementRequest request = AccommodationElementRequest.builder()
                    .baseElementRequest(base)
                    .location(newLocation)
                    .checkIn(checkIn)
                    .checkOut(checkOut)
                    .build();

            String expectedResponse = """
                    [
                      {
                        "baseElementID": "e4f56f0d-01ab-4ddb-be38-486ebefc4ede",
                        "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                        "elementType": "ACCOMMODATION",
                        "elementCategory": "Hotel",
                        "link": "https://book-hotel.ih/",
                        "price": %s,
                        "notes": "%s",
                        "status": null,
                        "order": 1,
                        "passengerDetailsList": [
                          {
                            "passengerId": "d2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f",
                            "firstName": "Clara",
                            "lastName": "Nguyen",
                            "avatar": "fox"
                          },
                          {
                            "passengerId": "ba9d1df2-99b1-4df4-ae00-c5d9ef6e5f57",
                            "firstName": "Ethan",
                            "lastName": "Brown",
                            "avatar": "turtle"
                          }
                        ],
                        "place": "Hotel Name",
                        "location": null,
                        "accommodationType": "CHECK_IN",
                        "dateTime": "%s"
                      },
                      {
                        "baseElementID": "e4f56f0d-01ab-4ddb-be38-486ebefc4ede",
                        "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                        "elementType": "ACCOMMODATION",
                        "elementCategory": "Hotel",
                        "link": "https://book-hotel.ih/",
                        "price": %s,
                        "notes": "%s",
                        "status": null,
                        "order": %s,
                        "passengerDetailsList": [
                          {
                            "passengerId": "d2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f",
                            "firstName": "Clara",
                            "lastName": "Nguyen",
                            "avatar": "fox"
                          },
                          {
                            "passengerId": "ba9d1df2-99b1-4df4-ae00-c5d9ef6e5f57",
                            "firstName": "Ethan",
                            "lastName": "Brown",
                            "avatar": "turtle"
                          }
                        ],
                        "place": "Hotel Name",
                        "location": null,
                        "accommodationType": "CHECK_OUT",
                        "dateTime": "2022-05-13T14:00:00"
                      }
                    ]
                    """.formatted(
                    newPrice,
                    newNotes,
                    newCheckInTime.format(formatter),
                    newPrice,
                    newNotes,
                    newCheckOutOrder
            );

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/accommodation",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    ACCOMMODATION_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].lastUpdatedAt").exists())
                    .andExpect(jsonPath("$[1].lastUpdatedAt").exists())
                    .andExpect(content().json(expectedResponse, JsonCompareMode.LENIENT))
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();

            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            String lastUpdatedAt = jsonResponse.get(0).get("lastUpdatedAt").asText();

            LocalDateTime updatedTime = LocalDateTime.parse(lastUpdatedAt);

            assertThat(updatedTime).isBetween(currentDateTime.minusSeconds(2), currentDateTime.plusSeconds(2));
        }
    }

    @Nested
    class UpdateOrder {

        @Test
        void bulkUpdateElementOrder() throws Exception {
            // using element IDs
            ElementOrderUpdateRequest transport = new ElementOrderUpdateRequest(
                    UUID.fromString("674a2a9c-2dc5-4d00-a9ee-e4f051a17194"), ElementType.TRANSPORT, 4  // org 2
            );
            ElementOrderUpdateRequest activity = new ElementOrderUpdateRequest(
                    UUID.fromString("82c24ee6-075f-4d8c-913d-1d06f325fd43"), ElementType.ACTIVITY, 3  // org 1
            );
            ElementOrderUpdateRequest accomm = new ElementOrderUpdateRequest(
                    UUID.fromString(ACCOMM_EVENT_ID), ElementType.ACCOMMODATION, 2  // org 1
            );

            List<ElementOrderUpdateRequest> request = List.of(transport, activity, accomm);

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/elements")
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/transport",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    TRANSPORT_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.order").value(4));

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/accommodation?type=CHECK_IN",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    ACCOMMODATION_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.order").value(2));

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/activity",
                                    SECTION_ONE,
                                    OPTION_TWO,
                                    ACTIVITY_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.order").value(3));
        }

        @ParameterizedTest
        @MethodSource("updateOrderRequestArgs")
        void shouldUpdateElementOrder(ElementType elementType, Optional<AccommodationType> accType, String baseElementId, String optionId) throws Exception {
            UpdateElementOrderRequest request = new UpdateElementOrderRequest(
                    elementType, 4, accType
            );

            mockMvc.perform(MockMvcRequestBuilders.put(
                                    "/v1/elements/{baseElementId}",
                                    baseElementId)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            String path = elementType.toString().toLowerCase();

            if (accType.isPresent()) {
                String param = "?type=" + accType.get();
                path += param;
            }

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/" + path,
                                    SECTION_ONE,
                                    optionId,
                                    baseElementId)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.order").value(4));
        }

        private static Stream<Arguments> updateOrderRequestArgs() {
            return Stream.of(
                    Arguments.of(ElementType.TRANSPORT, Optional.empty(), TRANSPORT_ELEMENT, OPTION_ONE),
                    Arguments.of(ElementType.ACTIVITY, Optional.empty(), ACTIVITY_ELEMENT, OPTION_TWO),
                    Arguments.of(ElementType.ACCOMMODATION, Optional.of(AccommodationType.CHECK_IN), ACCOMMODATION_ELEMENT, OPTION_ONE),
                    Arguments.of(ElementType.ACCOMMODATION, Optional.of(AccommodationType.CHECK_OUT), ACCOMMODATION_ELEMENT, OPTION_ONE)
            );
        }

        @Test
        void shouldNotUpdateElementOrder_whenNoOrderSpecified() throws Exception {
            UpdateElementOrderRequest request = new UpdateElementOrderRequest(
                    ElementType.TRANSPORT, null, null
            );

            mockMvc.perform(MockMvcRequestBuilders.put(
                                    "/v1/elements/{baseElementId}",
                                    TRANSPORT_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("Invalid request content."));
        }

        @Test
        void shouldNotUpdateElementOrder_whenNoElementTypeSpecified() throws Exception {
            UpdateElementOrderRequest request = new UpdateElementOrderRequest(
                    null, 2, null
            );

            mockMvc.perform(MockMvcRequestBuilders.put(
                                    "/v1/elements/{baseElementId}",
                                    ACTIVITY_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("Invalid request content."));
        }
    }

    @Nested
    class PassengerElement {
        @Test
        void shouldUpdateTransportElement() throws Exception {
            // Transport element has 4 passengers: 1-4

            BaseElementRequest base = BaseElementRequest.builder()
                    .passengerIds(List.of(PASSENGER_TWO_UUID, PASSENGER_THREE_UUID, PASSENGER_FIVE_UUID)) // removing 1st, and 4th, adding 5th
                    .build();

            TransportElementRequest request = TransportElementRequest.builder()
                    .baseElementRequest(base)
                    .build();

            String expectedResponse = """
                      {
                        "passengerDetailsList": [
                          {
                            "passengerId": "%s",
                            "firstName": "Alice",
                            "lastName": "Smith",
                            "avatar": "cat"
                          },
                          {
                            "passengerId": "%s",
                            "firstName": "Bob",
                            "lastName": "Johnson",
                            "avatar": "parrot"
                          },
                          {
                            "passengerId": "%s",
                            "firstName": "Ethan",
                            "lastName": "Brown",
                            "avatar": "turtle"
                          }
                        ]
                      }
                    """.formatted(PASSENGER_TWO, PASSENGER_THREE, PASSENGER_FIVE);

            mockMvc.perform(MockMvcRequestBuilders.put(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/transport",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    TRANSPORT_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponse, JsonCompareMode.LENIENT));
        }

        @Test
        void shouldUpdateActivityElement() throws Exception {
            BaseElementRequest base = BaseElementRequest.builder()
                    .passengerIds(List.of(PASSENGER_FOUR_UUID))
                    .build();

            ActivityElementRequest request = ActivityElementRequest.builder()
                    .baseElementRequest(base)
                    .build();

            String expectedResponse = """
                      {
                        "passengerDetailsList": [
                          {
                            "passengerId": "%s",
                            "firstName": "Clara",
                            "lastName": "Nguyen",
                            "avatar": "fox"
                          }
                        ]
                      }
                    """.formatted(PASSENGER_FOUR);

            mockMvc.perform(MockMvcRequestBuilders.put(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/activity",
                                    SECTION_ONE,
                                    OPTION_TWO,
                                    ACTIVITY_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponse, JsonCompareMode.LENIENT));
        }

        @Test
        void shouldUpdateAccommodationElement() throws Exception {
            BaseElementRequest base = BaseElementRequest.builder()
                    .passengerIds(List.of())
                    .build();

            AccommodationElementRequest request = AccommodationElementRequest.builder()
                    .baseElementRequest(base)
                    .build();

            String expectedResponse = """
                    [
                      {
                        "passengerDetailsList": []
                      },
                      {
                        "passengerDetailsList": []
                      }
                    ]
                    """;

            mockMvc.perform(MockMvcRequestBuilders.put(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/accommodation",
                                    SECTION_ONE,
                                    OPTION_ONE,
                                    ACCOMMODATION_ELEMENT)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponse, JsonCompareMode.LENIENT));
        }
    }

    @Disabled("Need to find a new way to test it as the mock db doesn't have cascade")
    @Nested
    class DeleteElement {


        @ParameterizedTest
        @MethodSource("deleteElementArgs")
        void shouldDeleteElement(ElementType elementType, String baseElementId, String optionId) throws Exception {
            String deleteParam = "?type=" + elementType;

            mockMvc.perform(MockMvcRequestBuilders.delete(
                                    BASE_ELEMENTS_URL + "/{baseElementId}" + deleteParam,
                                    SECTION_ONE,
                                    optionId,
                                    baseElementId)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            String getParam = elementType.toString().toLowerCase();

            if (elementType == ElementType.ACCOMMODATION) {
                getParam += "?type=" + AccommodationType.CHECK_IN;
            }

            mockMvc.perform(MockMvcRequestBuilders.get(
                                    BASE_ELEMENTS_URL + "/{baseElementId}/" + getParam,
                                    SECTION_ONE,
                                    optionId,
                                    baseElementId)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        private static Stream<Arguments> deleteElementArgs() {
            return Stream.of(
                    Arguments.of(ElementType.TRANSPORT, TRANSPORT_ELEMENT, OPTION_ONE),
                    Arguments.of(ElementType.ACTIVITY, ACTIVITY_ELEMENT, OPTION_TWO),
                    Arguments.of(ElementType.ACCOMMODATION, ACCOMMODATION_ELEMENT, OPTION_ONE)
            );
        }
    }
}