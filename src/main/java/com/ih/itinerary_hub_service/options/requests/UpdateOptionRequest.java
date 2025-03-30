package com.ih.itinerary_hub_service.options.requests;

import java.util.Optional;

public record UpdateOptionRequest(
        Optional<String> optionName, Optional<Integer> order
){
}
