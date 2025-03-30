package com.ih.itinerary_hub_service.elements.persistence.repository;

import com.ih.itinerary_hub_service.elements.persistence.entity.AccommodationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccommodationEventRepository extends JpaRepository<AccommodationEvent, UUID> {

    @Query("SELECT t FROM AccommodationEvent t WHERE t.accommodationElement.elementId = :accommodationId")
    List<AccommodationEvent> getAccommodationEventsByAccommodationId(UUID accommodationId);
}
