package com.ih.itinerary_hub_service.trips.exceptions;

public class TripNotFound extends RuntimeException {
    public TripNotFound(String message) {
        super(message);
    }
}
