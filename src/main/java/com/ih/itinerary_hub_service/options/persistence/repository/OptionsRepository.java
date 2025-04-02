package com.ih.itinerary_hub_service.options.persistence.repository;

import com.ih.itinerary_hub_service.options.persistence.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OptionsRepository extends JpaRepository<Option, UUID> {

    @Query("SELECT t FROM Option t WHERE t.optionId = :optionId AND t.section.sectionId = :sectionId")
    Optional<Option> findByOptionIdAndSectionId(@Param("optionId") UUID optionId, @Param("sectionId") UUID sectionId);

    @Query("SELECT t FROM Option t WHERE t.section.sectionId = :sectionId")
    List<Option> findBySectionId(@Param("sectionId") UUID sectionsId);
}
