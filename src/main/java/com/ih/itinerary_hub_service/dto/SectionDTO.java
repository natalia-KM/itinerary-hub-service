package com.ih.itinerary_hub_service.dto;

import com.ih.itinerary_hub_service.sections.responses.SectionDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SectionDTO {
    private SectionDetails sectionDetails;
    private List<OptionDTO> options;
}
