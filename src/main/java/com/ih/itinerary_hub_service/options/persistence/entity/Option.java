package com.ih.itinerary_hub_service.options.persistence.entity;

import com.ih.itinerary_hub_service.sections.persistence.entity.Section;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "options", schema = "dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Option {

    @Id
    @Column(name = "option_id", nullable = false)
    private UUID optionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(name = "option_name", nullable = false, length = 50)
    private String optionName;

    @Column(name = "option_order", nullable = false)
    private Integer optionOrder;
}
