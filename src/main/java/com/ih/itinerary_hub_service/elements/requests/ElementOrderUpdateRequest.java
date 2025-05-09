package com.ih.itinerary_hub_service.elements.requests;

import com.ih.itinerary_hub_service.elements.types.ElementType;

import java.util.UUID;

public record ElementOrderUpdateRequest(UUID elementId, ElementType elementType, Integer order) {
}
