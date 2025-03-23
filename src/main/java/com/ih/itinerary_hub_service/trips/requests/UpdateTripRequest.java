package com.ih.itinerary_hub_service.trips.requests;

import java.time.LocalDateTime;
import java.util.Optional;

public record UpdateTripRequest(
        Optional<String> tripName,
        Optional<LocalDateTime> startDate,
        Optional<LocalDateTime> endDate,
        Optional<String> imageRef
) {
}
