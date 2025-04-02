package com.ih.itinerary_hub_service.elements.types;

public enum ElementStatus {
    PENDING("PENDING"),
    BOOKED("BOOKED"),
    CANCELLED("CANCELLED"),
    EXPIRED("EXPIRED");

    public final String label;

    private ElementStatus(String label) {
        this.label = label;
    }
}
