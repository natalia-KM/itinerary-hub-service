package com.ih.itinerary_hub_service.elements.requests;

import com.ih.itinerary_hub_service.elements.types.AccommodationType;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record UpdateElementOrderRequest(
        @NotNull ElementType elementType,
        @NotNull Integer order,
        Optional<AccommodationType> accommodationType
        ) {
}
