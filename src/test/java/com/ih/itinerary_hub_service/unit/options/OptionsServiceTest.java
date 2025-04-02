package com.ih.itinerary_hub_service.unit.options;

import com.ih.itinerary_hub_service.dto.OptionDTO;
import com.ih.itinerary_hub_service.elements.service.ElementsService;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.options.exceptions.CreateOptionInvalidRequest;
import com.ih.itinerary_hub_service.options.persistence.entity.Option;
import com.ih.itinerary_hub_service.options.persistence.repository.OptionsRepository;
import com.ih.itinerary_hub_service.options.requests.CreateOptionRequest;
import com.ih.itinerary_hub_service.options.requests.UpdateOptionRequest;
import com.ih.itinerary_hub_service.options.responses.OptionDetails;
import com.ih.itinerary_hub_service.options.service.OptionsService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OptionsServiceTest {

    @Mock
    private ElementsService elementsService;

    @Mock
    private OptionsRepository optionsRepository;

    @InjectMocks
    private OptionsService optionsService;

    @Test
    void shouldSortOptions() {
        UUID option1Id = UUID.randomUUID();
        UUID option2Id = UUID.randomUUID();
        UUID option3Id = UUID.randomUUID();
        UUID option4Id = UUID.randomUUID();

        Option option1 = createNewOption(option1Id, 3);
        Option option2 = createNewOption(option2Id, 1);
        Option option3 = createNewOption(option3Id, 4);
        Option option4 = createNewOption(option4Id, 2);

        List<Option> options = Arrays.asList(option1, option2, option3, option4);

        when(optionsRepository.findBySectionId(MockData.sectionId)).thenReturn(options);
        when(elementsService.getElementsByIds(any())).thenReturn(List.of());

        List<OptionDTO> result = optionsService.findAllOptionDTOs(MockData.sectionId);

        assertNotNull(result);
        assertEquals(result.get(0).getOptionDetails().optionId(), option2Id);
        assertEquals(result.get(1).getOptionDetails().optionId(), option4Id);
        assertEquals(result.get(2).getOptionDetails().optionId(), option1Id);
        assertEquals(result.get(3).getOptionDetails().optionId(), option3Id);
    }

    @Test
    void shouldReturnEmptyLis_whenNoOptionsFound() {
        when(optionsRepository.findBySectionId(MockData.sectionId)).thenReturn(List.of());
        List<OptionDTO> result = optionsService.findAllOptionDTOs(MockData.sectionId);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void shouldMapToDTO() {
        Option option = createNewOption(UUID.randomUUID(), 1);
        OptionDetails expectedDetails = new OptionDetails(
                option.getOptionId(),
                "Option Name",
                1
        );

        when(optionsRepository.findByOptionIdAndSectionId(option.getOptionId(), MockData.sectionId)).thenReturn(Optional.of(option));

        OptionDetails optionDetails = optionsService.getOptionDetails(option.getOptionId(), MockData.sectionId);
        assertNotNull(optionDetails);
        assertEquals(expectedDetails, optionDetails);
    }

    private Option createNewOption(UUID optionId, Integer order) {
        return new Option(
                optionId,
                MockData.mockSection,
                "Option Name",
                order
        );
    }

    @Test
    void createOption_shouldThrow_whenOrderIsNull() {
        CreateOptionRequest request = new CreateOptionRequest("Name", null);

        assertThrows(CreateOptionInvalidRequest.class, () -> optionsService.createOption(MockData.mockSection, request));
    }

    @Test
    void createOption_shouldThrow_whenOrderIsInvalid() {
        CreateOptionRequest request = new CreateOptionRequest("Name", -1);

        assertThrows(CreateOptionInvalidRequest.class, () -> optionsService.createOption(MockData.mockSection, request));
    }

    @Test
    void createOption_shouldThrow_whenDbFails() {
        doThrow(IllegalArgumentException.class).when(optionsRepository).save(any(Option.class));
        assertThrows(DbFailure.class, () -> optionsService.createOption(MockData.mockSection, new CreateOptionRequest("Name", 1)));
    }

    @Test
    void updateOption_shouldThrow_whenDbFails() {
        when(optionsRepository.findByOptionIdAndSectionId(MockData.optionId, MockData.sectionId)).thenReturn(Optional.of(MockData.mockOption));

        doThrow(IllegalArgumentException.class).when(optionsRepository).save(any(Option.class));

        assertThrows(DbFailure.class, () -> optionsService.updateOption(MockData.optionId, MockData.sectionId, new UpdateOptionRequest(Optional.of("Name"), Optional.of(1))));
    }

    @Test
    void deleteOption_shouldThrow_whenDbFails() {
        when(optionsRepository.findByOptionIdAndSectionId(MockData.optionId, MockData.sectionId)).thenReturn(Optional.of(MockData.mockOption));

        doThrow(IllegalArgumentException.class).when(optionsRepository).delete(any(Option.class));

        assertThrows(DbFailure.class, () -> optionsService.deleteOption(MockData.optionId, MockData.sectionId));
    }
}