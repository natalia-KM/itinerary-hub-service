package com.ih.itinerary_hub_service.passengers.persistence.repository;

import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.passengers.persistence.entity.ElementPassenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ElementPassengerRepository extends JpaRepository<ElementPassenger, UUID> {
    List<ElementPassenger> findByBaseElement(BaseElement baseElement);

    void deleteByBaseElement(BaseElement baseElement);

    @Query("SELECT ep.passenger.passengerId FROM ElementPassenger ep WHERE ep.baseElement.baseElementId = :elementId")
    List<UUID> findPassengersIdsByElementId(@Param("elementId") UUID elementId);

    @Query("SELECT t from ElementPassenger t WHERE t.baseElement.baseElementId = :elementId AND t.passenger.passengerId = :passengerId")
    Optional<ElementPassenger> findByPassengerAndElementId(@Param("passengerId") UUID passengerId, @Param("elementId") UUID elementId);
}
