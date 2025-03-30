package com.ih.itinerary_hub_service.sections.requests;

import java.util.Optional;

public record UpdateSectionRequest(
        Optional<String> sectionName, Optional<Integer> order
) {
}
