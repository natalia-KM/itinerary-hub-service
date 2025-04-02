package com.ih.itinerary_hub_service.elements.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AccommodationEventRequest {
    private LocalDateTime dateTime;
    private Integer order;
}
