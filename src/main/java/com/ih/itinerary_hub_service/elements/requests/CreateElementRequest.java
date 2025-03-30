package com.ih.itinerary_hub_service.elements.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class CreateElementRequest {
    private BaseElementRequest baseElementRequest;
}
