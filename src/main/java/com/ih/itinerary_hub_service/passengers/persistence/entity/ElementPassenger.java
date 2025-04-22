package com.ih.itinerary_hub_service.passengers.persistence.entity;

import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "element_passengers", schema = "dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElementPassenger {

    @Id
    @Column(name = "id", nullable = false)
    private UUID elementPassengerId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(optional = false)
    @JoinColumn(name = "base_element_id", nullable = false)
    private BaseElement baseElement;
}
