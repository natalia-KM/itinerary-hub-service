package com.ih.itinerary_hub_service.users.requests;

import jakarta.validation.constraints.NotEmpty;

public record UserDetailsRequest(
        @NotEmpty
        String firstName,

        @NotEmpty
        String lastName
) {
}
