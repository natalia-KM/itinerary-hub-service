package com.ih.itinerary_hub_service.options.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOptionRequest(
        @NotBlank String optionName,
        @NotNull @Min(0) Integer order
) {
}
