package com.ih.itinerary_hub_service.elements.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class AccommodationElementRequest extends CreateElementRequest {
    private String place;
    private String location;
    private AccommodationEventRequest checkIn;
    private AccommodationEventRequest checkOut;
}
