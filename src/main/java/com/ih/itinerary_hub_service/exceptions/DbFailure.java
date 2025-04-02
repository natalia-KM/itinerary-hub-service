package com.ih.itinerary_hub_service.exceptions;

public class DbFailure extends RuntimeException {
    public DbFailure(String message) {
        super(message);
    }
}
