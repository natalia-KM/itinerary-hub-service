package com.ih.itinerary_hub_service.passengers.responses;

import java.util.UUID;

public record PassengerDetails(
        UUID passengerId,
        String firstName,
        String lastName,
        String avatar
) {
}
