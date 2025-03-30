package com.ih.itinerary_hub_service.elements.types;

public enum AccommodationType {
    CHECK_IN("CHECK_IN"),
    CHECK_OUT("CHECK_OUT");

    public final String label;

    private AccommodationType(String label) {
        this.label = label;
    }
}
