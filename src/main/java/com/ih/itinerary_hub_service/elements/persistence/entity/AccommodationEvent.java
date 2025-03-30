package com.ih.itinerary_hub_service.elements.persistence.entity;

import com.ih.itinerary_hub_service.elements.types.AccommodationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accommodation_events", schema = "dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationEvent {

    @Id
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private AccommodationElement accommodationElement;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccommodationType type;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "element_order", nullable = false)
    private Integer elementOrder;
}
