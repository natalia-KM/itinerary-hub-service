package com.ih.itinerary_hub_service.elements.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transport_elements", schema = "dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransportElement {

    @Id
    @Column(name = "element_id", nullable = false)
    private UUID elementId;

    @OneToOne(optional = false)
    @JoinColumn(name = "base_element_id", nullable = false)
    private BaseElement baseElement;

    @Column(name = "origin_place", nullable = false, length = 100)
    private String originPlace;

    @Column(name = "origin_datetime", nullable = false)
    private LocalDateTime originDateTime;

    @Column(name = "destination_place", nullable = false, length = 100)
    private String destinationPlace;

    @Column(name = "destination_datetime", nullable = false)
    private LocalDateTime destinationDateTime;

    @Column(name = "provider", length = 100)
    private String provider;

    @Column(name = "element_order", nullable = false)
    private Integer elementOrder;
}
