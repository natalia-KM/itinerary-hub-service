package com.ih.itinerary_hub_service.elements.exceptions;

public class ElementDoesNotExist extends RuntimeException {
    public ElementDoesNotExist(String message) {
        super(message);
    }
}
