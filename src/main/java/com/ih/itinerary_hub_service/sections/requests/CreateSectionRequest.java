package com.ih.itinerary_hub_service.sections.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSectionRequest(
        @NotBlank String sectionName,
        @NotNull @Min(0) Integer order) {
}
