package com.ih.itinerary_hub_service.elements.requests;

import com.ih.itinerary_hub_service.elements.types.ElementStatus;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseElementRequest {
    private ElementType elementType;
    private String link;
    private BigDecimal price;
    private String notes;
    private ElementStatus status;
}
