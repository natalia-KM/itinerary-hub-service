package com.ih.itinerary_hub_service.elements.persistence.repository;

import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BaseElementRepository extends JpaRepository<BaseElement, UUID> {

    @Query("SELECT t FROM BaseElement t WHERE t.option.optionId = :optionId")
    List<BaseElement> findByOptionId(@Param("optionId") UUID optionId);

    @Query("SELECT t FROM BaseElement t WHERE t.baseElementId = :baseElementId AND t.option.optionId = :optionId")
    Optional<BaseElement> findByBaseIdAndOptionId(@Param("baseElementId") UUID baseElementId, @Param("optionId") UUID optionId);
}
