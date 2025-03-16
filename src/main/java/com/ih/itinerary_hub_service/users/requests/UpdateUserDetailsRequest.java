package com.ih.itinerary_hub_service.users.requests;

import jakarta.annotation.Nullable;

public record UpdateUserDetailsRequest(
         String firstName,
         String lastName,
         @Nullable String currency
) {
}
