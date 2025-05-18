package com.ih.itinerary_hub_service.elements.requests;

import com.ih.itinerary_hub_service.elements.types.AccommodationType;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

public record MoveElementRequest(
        @NotNull UUID newOptionId,
        @NotNull UUID newSectionId,
        @NotNull ElementType elementType,
        Optional<AccommodationType> accommodationType
) {
}
