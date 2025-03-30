package com.ih.itinerary_hub_service.sections.responses;

import java.util.UUID;

public record SectionDetails(
        UUID sectionId,
        String sectionName,
        Integer order
) {
}
