package com.ih.itinerary_hub_service.trips.exceptions;

public class TripsDbFailure extends RuntimeException {
    public TripsDbFailure(String message) {
        super(message);
    }
}
