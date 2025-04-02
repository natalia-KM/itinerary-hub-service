package com.ih.itinerary_hub_service.options.responses;

import java.util.UUID;

public record OptionDetails(
        UUID optionId,
        String optionName,
        Integer order
) {
}
