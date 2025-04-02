package com.ih.itinerary_hub_service.dto;

import com.ih.itinerary_hub_service.elements.model.BaseElementDetails;
import com.ih.itinerary_hub_service.options.responses.OptionDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OptionDTO {
    private OptionDetails optionDetails;
    private List<BaseElementDetails> baseElementDetails;
}
