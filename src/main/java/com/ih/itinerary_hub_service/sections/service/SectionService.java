package com.ih.itinerary_hub_service.sections.service;

import com.ih.itinerary_hub_service.dto.OptionDTO;
import com.ih.itinerary_hub_service.dto.SectionDTO;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.options.responses.OptionDetails;
import com.ih.itinerary_hub_service.options.service.OptionsService;
import com.ih.itinerary_hub_service.sections.exceptions.CreateSectionInvalidRequest;
import com.ih.itinerary_hub_service.sections.exceptions.SectionNotFound;
import com.ih.itinerary_hub_service.sections.persistence.entity.Section;
import com.ih.itinerary_hub_service.sections.persistence.repository.SectionsRepository;
import com.ih.itinerary_hub_service.sections.requests.CreateSectionRequest;
import com.ih.itinerary_hub_service.sections.requests.UpdateSectionRequest;
import com.ih.itinerary_hub_service.sections.responses.SectionDetails;
import com.ih.itinerary_hub_service.trips.persistence.entity.Trip;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SectionService {

    private final SectionsRepository sectionsRepository;
    private final OptionsService optionsService;

    public SectionService(SectionsRepository sectionsRepository, OptionsService optionsService) {
        this.sectionsRepository = sectionsRepository;
        this.optionsService = optionsService;
    }

    public SectionDetails createSection(Trip trip, CreateSectionRequest request) {
        if(request.order() == null || request.order() < 0) {
            throw new CreateSectionInvalidRequest("Order cannot be null");
        }

        if(request.sectionName().isBlank()) {
            throw new CreateSectionInvalidRequest("Section name cannot be empty");
        }

        UUID sectionId = UUID.randomUUID();
        Section newSection = new Section(
                sectionId,
                trip,
                request.sectionName().trim(),
                request.order()
        );

        try {
            sectionsRepository.save(newSection);
            log.info("Section created: {}", sectionId);
            return mapSectionDetails(newSection);
        } catch (Exception e) {
            log.error("Failed to create a section: {}", e.getMessage());
            throw new DbFailure("Failed to create a section");
        }
    }

    public SectionDetails getSectionDetails(UUID sectionId, UUID tripId) {
        Section existingSection = getSection(sectionId, tripId);

        return mapSectionDetails(existingSection);
    }

    public void updateSection(UUID sectionId, UUID tripId, UpdateSectionRequest request) {
        Section existingSection = getSection(sectionId, tripId);

        request.sectionName()
                .filter(name -> !name.isBlank())
                .ifPresent(existingSection::setSectionName);

        request.order().ifPresent(existingSection::setSectionOrder);

        try {
            sectionsRepository.save(existingSection);
            log.info("Section updated: {}", sectionId);
        } catch (Exception e) {
            log.error("Failed to update a section: {}", e.getMessage());
            throw new DbFailure("Failed to update a section");
        }
    }

    public void deleteSection(UUID sectionId, UUID tripId) {
        Section existingSection = getSection(sectionId, tripId);

        try {
            sectionsRepository.delete(existingSection);
            log.info("Section deleted: {}", sectionId);
        } catch (Exception e) {
            log.error("Failed to delete a section: {}", e.getMessage());
            throw new DbFailure("Failed to delete a section");
        }
    }

    public Section getSection(UUID sectionId, UUID tripId) {
        return sectionsRepository.findBySectionIdAndTripId(sectionId, tripId)
                .orElseThrow(() -> {
                    log.error("Section not found with ID: {} and tripId: {}", sectionId, tripId);
                    return new SectionNotFound("Section not found");
                });
    }

    @Transactional
    public void updateSectionOrders(UUID tripId, List<SectionDetails> updatedSections) {
        for(SectionDetails sectionDetails : updatedSections) {
            sectionsRepository.updateOrder(sectionDetails.sectionId(), tripId, sectionDetails.order());
        }
    }

    public List<SectionDetails> getSections(UUID tripId) {
        return sectionsRepository.findByTripId(tripId).stream()
                .map(this::mapSectionDetails)
                .sorted(Comparator.comparing(SectionDetails::order))
                .collect(Collectors.toList());
    }

    public List<SectionDTO> getAllSectionDTOs(UUID tripId) {
        List<Section> sections = sectionsRepository.findByTripId(tripId);
        List<SectionDTO> sectionDTOs = new ArrayList<>();

        for(Section section : sections) {
            List<OptionDTO> options = optionsService.findAllOptionDTOs(section.getSectionId());
            sectionDTOs.add(new SectionDTO(
                    mapSectionDetails(section),
                    options
            ));
        }

        sectionDTOs.sort(Comparator.comparing(o -> o.getSectionDetails().order()));
        return sectionDTOs;
    }

    private SectionDetails mapSectionDetails(Section section) {
        return new SectionDetails(
                section.getSectionId(),
                section.getSectionName(),
                section.getSectionOrder()
        );
    }
}
