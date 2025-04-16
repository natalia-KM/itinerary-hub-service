package com.ih.itinerary_hub_service.passengers.requests;

import jakarta.validation.constraints.NotBlank;

public record CreatePassengerRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String avatar
) {
}
