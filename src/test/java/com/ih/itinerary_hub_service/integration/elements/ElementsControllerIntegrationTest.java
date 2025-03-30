package com.ih.itinerary_hub_service.integration.elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ih.itinerary_hub_service.elements.requests.*;
import com.ih.itinerary_hub_service.elements.service.ElementsService;
import com.ih.itinerary_hub_service.elements.types.ElementStatus;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.integration.BaseIntegrationTest;
import com.ih.itinerary_hub_service.options.persistence.repository.OptionsRepository;
import com.ih.itinerary_hub_service.sections.persistence.repository.SectionsRepository;
import com.ih.itinerary_hub_service.trips.persistence.repository.TripsRepository;
import com.ih.itinerary_hub_service.users.persistence.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    private static final String CREATE_ELEMENTS_URL = "/v1/sections/{sectionId}/options/{optionId}/elements";
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
                                    CREATE_ELEMENTS_URL + "/transport",
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
                                    CREATE_ELEMENTS_URL + "/activity",
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
                                    CREATE_ELEMENTS_URL + "/accommodation",
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

}