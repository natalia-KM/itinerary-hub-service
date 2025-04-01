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
import com.ih.itinerary_hub_service.sections.persistence.repository.SectionsRepository;
import com.ih.itinerary_hub_service.trips.persistence.repository.TripsRepository;
import com.ih.itinerary_hub_service.users.persistence.repository.UserRepository;
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

    private static final String BASE_ELEMENTS_URL = "/v1/sections/{sectionId}/options/{optionId}/elements";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

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
                    null,
                    BigDecimal.valueOf(23.45),
                    "Notes",
                    ElementStatus.PENDING
            );


            TransportElementRequest request = TransportElementRequest.builder()
                    .baseElementRequest(base)
                    .originPlace(originPlace)
                    .destinationPlace(destinationPlace)
                    .originDateTime(originTime)
                    .destinationDateTime(destinationTime)
                    .order(order)
                    .build();


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
                    .andExpect(jsonPath("$.optionID").exists())
                    .andExpect(jsonPath("$.lastUpdatedAt").exists())
                    .andExpect(jsonPath("$.elementType").value(ElementType.TRANSPORT.toString()))
                    .andExpect(jsonPath("$.link").isEmpty())
                    .andExpect(jsonPath("$.price").value(23.45))
                    .andExpect(jsonPath("$.notes").value("Notes"))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.order").value(order))
                    .andExpect(jsonPath("$.originPlace").value(originPlace))
                    .andExpect(jsonPath("$.destinationPlace").value(destinationPlace))
                    .andExpect(jsonPath("$.originDateTime").value(originTime.format(formatter)))
                    .andExpect(jsonPath("$.destinationDateTime").value(destinationTime.format(formatter)))
                    .andExpect(jsonPath("$.provider").isEmpty());
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
                    link,
                    BigDecimal.valueOf(23.45),
                    "Notes",
                    ElementStatus.PENDING
            );


            ActivityElementRequest request = ActivityElementRequest.builder()
                    .baseElementRequest(base)
                    .activityName(activityName)
                    .location(location)
                    .startsAt(startsAt)
                    .duration(null)
                    .order(order)
                    .build();

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
                    .andExpect(jsonPath("$.optionID").exists())
                    .andExpect(jsonPath("$.lastUpdatedAt").exists())
                    .andExpect(jsonPath("$.elementType").value(ElementType.ACTIVITY.toString()))
                    .andExpect(jsonPath("$.link").value(link))
                    .andExpect(jsonPath("$.price").value(23.45))
                    .andExpect(jsonPath("$.notes").value("Notes"))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.order").value(order))
                    .andExpect(jsonPath("$.activityName").value(activityName))
                    .andExpect(jsonPath("$.location").value(location))
                    .andExpect(jsonPath("$.startsAt").value(startsAt.format(formatter)))
                    .andExpect(jsonPath("$.duration").isEmpty());
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
                    link,
                    null,
                    null,
                    null
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
                        "elementType": "ACCOMMODATION",
                        "link": "https://some-link.co",
                        "price": null,
                        "notes": null,
                        "status": null,
                        "order": 1,
                        "place": "place",
                        "location": "location",
                        "accommodationType": "CHECK_IN",
                        "dateTime": "2025-03-22T00:00:00"
                      },
                      {
                        "elementType": "ACCOMMODATION",
                        "link": "https://some-link.co",
                        "price": null,
                        "notes": null,
                        "status": null,
                        "order": 3,
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
                    .andExpect(jsonPath("$[0].optionID").exists())
                    .andExpect(jsonPath("$[0].lastUpdatedAt").exists())
                    .andExpect(jsonPath("$[1].baseElementID").exists())
                    .andExpect(jsonPath("$[1].optionID").exists())
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
                          "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                          "lastUpdatedAt": "2022-05-12T00:00:00",
                          "elementType": "TRANSPORT",
                          "link": null,
                          "price": 23.45,
                          "notes": "Notes",
                          "status": "PENDING",
                          "order": 2,
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
                    .andExpect(content().json(expectedResponse, JsonCompareMode.LENIENT));
        }

        @Test
        void shouldGetActivityElement() throws Exception {
            String expectedResponse = """
                        {
                          "baseElementID": "b647b387-31ad-4ffb-a9d2-91551d4b3138",
                          "optionID": "eb7fd861-6dba-4893-a4c8-bac1bd5a47ba",
                          "lastUpdatedAt": "2022-05-20T22:00:00",
                          "elementType": "ACTIVITY",
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
                    .andExpect(content().json(expectedResponse, JsonCompareMode.LENIENT));
        }

        @Test
        void shouldGetAccommElement() throws Exception {
            String expectedResponse = """
                        {
                          "baseElementID": "e4f56f0d-01ab-4ddb-be38-486ebefc4ede",
                          "optionID": "0d78ebf0-0159-4843-b54b-a696644f26fc",
                          "lastUpdatedAt": "2022-05-15T00:00:00",
                          "elementType": "ACCOMMODATION",
                          "link": "https://book-hotel.ih/",
                          "price": null,
                          "notes": null,
                          "status": null,
                          "order": 1,
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
                    .andExpect(content().json(expectedResponse, JsonCompareMode.LENIENT));
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
                    .build();

            TransportElementRequest request = TransportElementRequest.builder()
                    .baseElementRequest(base)
                    .originPlace(newOriginPlace)
                    .destinationDateTime(newDestinationTime)
                    .provider(newProvider)
                    .build();

            String expectedResponse = """
                      {
                        "elementType": "TRANSPORT",
                        "link": "%s",
                        "price": %s,
                        "notes": "Notes",
                        "status": "PENDING",
                        "order": 2,
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
                    .andExpect(jsonPath("$.baseElementID").exists())
                    .andExpect(jsonPath("$.optionID").exists())
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
                        "link": null,
                        "price": 1000,
                        "notes": null,
                        "status": "%s",
                        "order": 1,
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
                    .andExpect(jsonPath("$.baseElementID").exists())
                    .andExpect(jsonPath("$.optionID").exists())
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
                            "elementType": "ACCOMMODATION",
                            "link": "https://book-hotel.ih/",
                            "price": %s,
                            "notes": "%s",
                            "status": null,
                            "order": 1,
                            "place": "Hotel Name",
                            "location": null,
                            "accommodationType": "CHECK_IN",
                            "dateTime": "%s"
                          },
                          {
                            "elementType": "ACCOMMODATION",
                            "link": "https://book-hotel.ih/",
                            "price": %s,
                            "notes": "%s",
                            "status": null,
                            "order": %s,
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
                    .andExpect(jsonPath("$[0].baseElementID").exists())
                    .andExpect(jsonPath("$[0].optionID").exists())
                    .andExpect(jsonPath("$[0].lastUpdatedAt").exists())
                    .andExpect(jsonPath("$[1].baseElementID").exists())
                    .andExpect(jsonPath("$[1].optionID").exists())
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

            if(accType.isPresent()) {
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

            if(elementType == ElementType.ACCOMMODATION) {
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