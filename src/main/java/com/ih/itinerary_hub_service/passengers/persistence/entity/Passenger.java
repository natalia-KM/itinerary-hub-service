package com.ih.itinerary_hub_service.passengers.persistence.entity;

import com.ih.itinerary_hub_service.users.persistence.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "passengers", schema = "dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    @Id
    @Column(name = "passenger_id", nullable = false)
    private UUID passengerId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "avatar", nullable = false)
    private String avatar = "default";

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
