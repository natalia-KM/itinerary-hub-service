package com.ih.itinerary_hub_service.unit.sections;

import com.ih.itinerary_hub_service.dto.SectionDTO;
import com.ih.itinerary_hub_service.options.service.OptionsService;
import com.ih.itinerary_hub_service.sections.persistence.entity.Section;
import com.ih.itinerary_hub_service.sections.persistence.repository.SectionsRepository;
import com.ih.itinerary_hub_service.sections.responses.SectionDetails;
import com.ih.itinerary_hub_service.sections.service.SectionService;
import com.ih.itinerary_hub_service.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {
    @Mock
    private SectionsRepository sectionsRepository;

    @Mock
    private OptionsService optionsService;

    @InjectMocks
    private SectionService sectionService;


    @Test
    void shouldSortSections() {
        UUID section1Id = UUID.randomUUID();
        UUID section2Id = UUID.randomUUID();
        UUID section3Id = UUID.randomUUID();
        UUID section4Id = UUID.randomUUID();

        Section section1 = createNewSection(section1Id, 3);
        Section section2 = createNewSection(section2Id, 1);
        Section section3 = createNewSection(section3Id, 4);
        Section section4 = createNewSection(section4Id, 2);

        List<Section> sections = Arrays.asList(section1, section2, section3, section4);

        when(sectionsRepository.findByTripId(MockData.tripId)).thenReturn(sections);
        when(optionsService.findAllOptionDTOs(any())).thenReturn(List.of());

        List<SectionDTO> result = sectionService.getAllSectionDTOs(MockData.tripId);

        assertNotNull(result);
        assertEquals(result.get(0).getSectionDetails().sectionId(), section2Id);
        assertEquals(result.get(1).getSectionDetails().sectionId(), section4Id);
        assertEquals(result.get(2).getSectionDetails().sectionId(), section1Id);
        assertEquals(result.get(3).getSectionDetails().sectionId(), section3Id);
    }

    @Test
    void shouldReturnEmptyList_whenNoSectionsFound() {
        when(sectionsRepository.findByTripId(MockData.tripId)).thenReturn(List.of());

        List<SectionDTO> result = sectionService.getAllSectionDTOs(MockData.tripId);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void shouldMapToDTO() {
        Section section = createNewSection(UUID.randomUUID(), 3);
        SectionDetails expectedDetails = new SectionDetails(
                section.getSectionId(),
                "Section Name",
                3
        );

        when(sectionsRepository.findBySectionIdAndTripId(section.getSectionId(), MockData.tripId)).thenReturn(Optional.of(section));

        SectionDetails sectionDetails = sectionService.getSectionDetails(section.getSectionId(), MockData.tripId);
        assertNotNull(sectionDetails);
        assertEquals(expectedDetails, sectionDetails);
    }

    private Section createNewSection(UUID sectionId, Integer order) {
        return new Section(
                sectionId,
                MockData.mockTrip,
                "Section Name",
                order
        );
    }
}