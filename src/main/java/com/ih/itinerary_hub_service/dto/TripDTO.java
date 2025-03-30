package com.ih.itinerary_hub_service.dto;

import com.ih.itinerary_hub_service.trips.responses.TripDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TripDTO {
    private TripDetails tripDetails;
    private List<SectionDTO> sections;
}
