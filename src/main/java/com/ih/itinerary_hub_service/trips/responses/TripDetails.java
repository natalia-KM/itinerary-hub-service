package com.ih.itinerary_hub_service.trips.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripDetails(
        UUID tripId,
        String tripName,
        LocalDateTime createdAt,
        String imageRef,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
