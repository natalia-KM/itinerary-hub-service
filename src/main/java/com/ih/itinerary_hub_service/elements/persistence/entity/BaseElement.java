package com.ih.itinerary_hub_service.elements.persistence.entity;

import com.ih.itinerary_hub_service.elements.types.ElementStatus;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.options.persistence.entity.Option;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "base_elements", schema = "dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseElement {

    @Id
    @Column(name = "base_element_id", nullable = false)
    private UUID baseElementId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "element_type", nullable = false)
    private ElementType elementType;

    @Column(name = "element_category", nullable = false)
    private String elementCategory;

    @Column(name = "link")
    private String link;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "notes")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "element_status")
    private ElementStatus status;
}
