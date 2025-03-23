package com.ih.itinerary_hub_service.trips.requests;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.Optional;

public record CreateTripRequest(
        @NotEmpty String tripName,
        Optional<LocalDateTime> startDate,
        Optional<LocalDateTime> endDate,
        Optional<String> imageRef
) {
}
