package com.ih.itinerary_hub_service.elements.types;

import lombok.Getter;

@Getter
public enum ElementType {
    ACTIVITY("ACTIVITY"),
    TRANSPORT("TRANSPORT"),
    ACCOMMODATION("ACCOMMODATION");

    public final String label;

    private ElementType(String label) {
        this.label = label;
    }
}
