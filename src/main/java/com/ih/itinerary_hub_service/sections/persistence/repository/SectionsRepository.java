package com.ih.itinerary_hub_service.sections.persistence.repository;

import com.ih.itinerary_hub_service.sections.persistence.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SectionsRepository extends JpaRepository<Section, UUID> {

    @Query("SELECT t FROM Section t WHERE t.sectionId = :sectionId AND t.trip.tripId = :tripId")
    Optional<Section> findBySectionIdAndTripId(@Param("sectionId") UUID sectionId, @Param("tripId") UUID tripId);

    @Query("SELECT t FROM Section t WHERE t.trip.tripId = :tripId")
    List<Section> findByTripId(@Param("tripId") UUID tripId);


}
