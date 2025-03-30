package com.ih.itinerary_hub_service.elements.persistence.repository;

import com.ih.itinerary_hub_service.elements.persistence.entity.ActivityElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityElement, UUID> {

    @Query("SELECT t FROM ActivityElement t WHERE t.baseElement.baseElementId = :baseElementId")
    Optional<ActivityElement> getActivityElementByBaseId(@Param("baseElementId") UUID baseElementId);

}
