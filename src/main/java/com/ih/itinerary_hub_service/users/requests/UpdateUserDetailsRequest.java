package com.ih.itinerary_hub_service.users.requests;

import java.util.Optional;

public record UpdateUserDetailsRequest(
        Optional<String> firstName,
        Optional<String> lastName,
        Optional<String> currency
) {
}
