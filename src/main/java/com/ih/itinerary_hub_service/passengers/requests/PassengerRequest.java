package com.ih.itinerary_hub_service.passengers.requests;

public record PassengerRequest(
        String firstName,
        String lastName,
        String avatar
) {
}
