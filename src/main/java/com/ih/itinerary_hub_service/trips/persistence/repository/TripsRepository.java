package com.ih.itinerary_hub_service.trips.persistence.repository;

import com.ih.itinerary_hub_service.trips.persistence.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripsRepository extends JpaRepository<Trip, UUID> {

    @Query("SELECT t FROM Trip t WHERE t.user.userId = :userId")
    List<Trip> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM Trip t WHERE t.tripId = :tripId AND t.user.userId = :userId")
    Optional<Trip> findByTripIdAndUserId(@Param("tripId") UUID tripId, @Param("userId") UUID userId);
}
