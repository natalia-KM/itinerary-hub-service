package com.ih.itinerary_hub_service.elements.requests;

import com.ih.itinerary_hub_service.elements.types.ElementStatus;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseElementRequest {
    private ElementType elementType;
    private String elementCategory;
    private String link;
    private BigDecimal price;
    private String notes;
    private ElementStatus status;
    private List<UUID> passengerIds;
}
