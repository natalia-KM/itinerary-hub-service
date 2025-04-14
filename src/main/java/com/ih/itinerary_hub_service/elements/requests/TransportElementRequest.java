package com.ih.itinerary_hub_service.elements.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TransportElementRequest extends CreateElementRequest {
    private String originPlace;
    private String destinationPlace;
    private LocalDateTime originDateTime;
    private LocalDateTime destinationDateTime;
    private String originProvider;
    private String destinationProvider;
    private Integer order;
}
