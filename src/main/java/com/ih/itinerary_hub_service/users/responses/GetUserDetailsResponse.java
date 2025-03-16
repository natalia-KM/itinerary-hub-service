package com.ih.itinerary_hub_service.users.responses;

import java.util.Optional;

public record GetUserDetailsResponse(String firstName, String lastName, boolean isGuest, Optional<String> currency) {
}
