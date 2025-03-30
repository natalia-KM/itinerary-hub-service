package com.ih.itinerary_hub_service.elements.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_elements", schema = "dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityElement {

    @Id
    @Column(name = "element_id", nullable = false)
    private UUID elementId;

    @OneToOne(optional = false)
    @JoinColumn(name = "base_element_id", nullable = false)
    private BaseElement baseElement;

    @Column(name = "activity_name", nullable = false)
    private String activityName;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "element_order", nullable = false)
    private Integer elementOrder;
}
