package com.ih.itinerary_hub_service.passengers.persistence.repository;

import com.ih.itinerary_hub_service.passengers.persistence.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PassengersRepository extends JpaRepository<Passenger, UUID> {

    @Query("SELECT p FROM Passenger p WHERE p.user.userId = :userId")
    List<Passenger> findByUserId(@Param("userId") UUID userId);
}
