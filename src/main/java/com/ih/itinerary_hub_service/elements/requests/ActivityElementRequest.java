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
public class ActivityElementRequest extends CreateElementRequest {
    private String activityName;
    private String location;
    private LocalDateTime startsAt;
    private Integer duration;
    private Integer order;
}
