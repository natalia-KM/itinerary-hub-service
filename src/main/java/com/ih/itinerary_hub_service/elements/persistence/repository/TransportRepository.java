package com.ih.itinerary_hub_service.elements.persistence.repository;

import com.ih.itinerary_hub_service.elements.persistence.entity.TransportElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransportRepository extends JpaRepository<TransportElement, UUID> {

    @Query("SELECT t FROM TransportElement t WHERE t.baseElement.baseElementId = :baseElementId")
    Optional<TransportElement> getTransportElementByBaseId(@Param("baseElementId") UUID baseElementId);
}
