package com.ih.itinerary_hub_service.elements.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "accommodation_elements", schema = "dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationElement {

    @Id
    @Column(name = "element_id", nullable = false)
    private UUID elementId;

    @OneToOne(optional = false)
    @JoinColumn(name = "base_element_id", nullable = false)
    private BaseElement baseElement;

    @Column(name = "place", nullable = false, length = 150)
    private String place;

    @Column(name = "location", length = 100)
    private String location;
}
