package com.ih.itinerary_hub_service.users.responses;

import java.time.LocalDateTime;
import java.util.Optional;

public record GetUserDetailsResponse(
        String firstName,
        String lastName,
        boolean isGuest,
        LocalDateTime createdAt,
        Optional<String> currency
) {
}
