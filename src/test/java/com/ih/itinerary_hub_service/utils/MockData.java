package com.ih.itinerary_hub_service.utils;

import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.requests.*;
import com.ih.itinerary_hub_service.elements.types.ElementStatus;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.options.persistence.entity.Option;
import com.ih.itinerary_hub_service.sections.persistence.entity.Section;
import com.ih.itinerary_hub_service.trips.persistence.entity.Trip;
import com.ih.itinerary_hub_service.users.persistence.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MockData {

    public static final UUID userId = UUID.randomUUID();
    public static final UUID tripId = UUID.randomUUID();
    public static final UUID sectionId = UUID.randomUUID();
    public static final UUID optionId = UUID.randomUUID();

    public static final LocalDateTime DATE = LocalDateTime.of(2024, 5, 12, 0, 0, 0);
    public static final String tripName = "Dubai Trip";
    public static final String imageRef = "image-1";

    public static final User mockUser = new User(
            userId,
            "John",
            "Doe",
            LocalDateTime.now(),
            true,
            null,
            null,
            null
    );
    public static final Trip mockTrip = new Trip(tripId, mockUser, tripName, DATE, DATE, DATE, imageRef);

    public static final Section mockSection = new Section(sectionId, mockTrip, "Section Name", 1);

    public static final Option mockOption = new Option(optionId, mockSection, "Option Name", 1);

    static BaseElementRequest transportBase = getNewBaseElementRequest(ElementType.TRANSPORT);
    static BaseElementRequest activityBase = getNewBaseElementRequest(ElementType.TRANSPORT);
    static BaseElementRequest accommBase = getNewBaseElementRequest(ElementType.TRANSPORT);

    public static TransportElementRequest mockTransportRequest = TransportElementRequest.builder()
            .baseElementRequest(transportBase)
            .originPlace("origin")
            .destinationPlace("destination")
            .originDateTime(LocalDateTime.now())
            .destinationDateTime(LocalDateTime.now().plusDays(1))
            .order(1)
            .build();

    static AccommodationEventRequest checkIn = new AccommodationEventRequest(LocalDateTime.now(), 1);
    static AccommodationEventRequest checkOut = new AccommodationEventRequest(LocalDateTime.now().plusDays(1), 2);

    public static AccommodationElementRequest mockAccomRequest = AccommodationElementRequest.builder()
            .baseElementRequest(accommBase)
            .place("place")
            .location("location")
            .checkIn(checkIn)
            .checkOut(checkOut)
            .build();

    public static ActivityElementRequest mockActivityRequest = ActivityElementRequest.builder()
            .baseElementRequest(activityBase)
            .activityName("activity")
            .location("location")
            .startsAt(LocalDateTime.now())
            .duration(null)
            .order(1)
            .build();

    public static BaseElement getNewBaseElement(UUID baseElementId, ElementType type) {
        LocalDateTime dateTime = LocalDateTime.now();

        return new BaseElement(
                baseElementId,
                MockData.mockOption,
                dateTime,
                type,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static BaseElementRequest getNewBaseElementRequest(ElementType type) {
        return new BaseElementRequest(
                type,
                null,
                null,
                BigDecimal.valueOf(23.45),
                "Notes",
                ElementStatus.PENDING,
                List.of()
        );
    }
}
