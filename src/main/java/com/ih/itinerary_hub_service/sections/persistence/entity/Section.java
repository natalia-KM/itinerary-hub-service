package com.ih.itinerary_hub_service.sections.persistence.entity;

import com.ih.itinerary_hub_service.trips.persistence.entity.Trip;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "sections", schema = "dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Section {

    @Id
    @Column(name = "section_id", nullable = false)
    private UUID sectionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "section_name", nullable = false, length = 100)
    private String sectionName;

    @Column(name = "section_order", nullable = false)
    private Integer sectionOrder;
}
