package com.ih.itinerary_hub_service.options.service;

import com.ih.itinerary_hub_service.dto.OptionDTO;
import com.ih.itinerary_hub_service.elements.model.BaseElementDetails;
import com.ih.itinerary_hub_service.elements.service.ElementsService;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.options.exceptions.CreateOptionInvalidRequest;
import com.ih.itinerary_hub_service.options.exceptions.OptionNotFound;
import com.ih.itinerary_hub_service.options.persistence.entity.Option;
import com.ih.itinerary_hub_service.options.persistence.repository.OptionsRepository;
import com.ih.itinerary_hub_service.options.requests.CreateOptionRequest;
import com.ih.itinerary_hub_service.options.requests.UpdateOptionRequest;
import com.ih.itinerary_hub_service.options.responses.OptionDetails;
import com.ih.itinerary_hub_service.sections.persistence.entity.Section;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OptionsService {

    private final OptionsRepository optionsRepository;
    private final ElementsService elementsService;

    public OptionsService(OptionsRepository optionsRepository, ElementsService elementsService) {
        this.optionsRepository = optionsRepository;
        this.elementsService = elementsService;
    }

    public OptionDetails createOption(Section existingSection, CreateOptionRequest request) {
        if(request.order() == null || request.order() < 0) {
            throw new CreateOptionInvalidRequest("Order cannot be null");
        }

        if(request.optionName().isBlank()) {
            throw new CreateOptionInvalidRequest("Option name cannot be empty");
        }

        UUID optionId = UUID.randomUUID();
        Option newOption = new Option(
                optionId,
                existingSection,
                request.optionName(),
                request.order()
        );

        try {
            optionsRepository.save(newOption);
            log.info("Option created: {}", optionId);
            return mapOptionDetails(newOption);
        } catch (Exception e) {
            log.error("Failed to create an option: {}", e.getMessage());
            throw new DbFailure("Failed to create an option");
        }
    }

    public OptionDetails getOptionDetails(UUID optionId, UUID sectionId) {
        Option existingOption = getOption(optionId, sectionId);

        return mapOptionDetails(existingOption);
    }

    public void updateOption(UUID optionId, UUID sectionId, UpdateOptionRequest request) {
        Option existingOption = getOption(optionId, sectionId);

        request.optionName()
                .filter(name -> !name.isBlank())
                .ifPresent(existingOption::setOptionName);

        request.order().ifPresent(existingOption::setOptionOrder);

        try {
            optionsRepository.save(existingOption);
            log.info("Option updated: {}", existingOption.getOptionId());
        } catch (Exception e) {
            log.error("Failed to update an option: {}", e.getMessage());
            throw new DbFailure("Failed to update an option");
        }
    }

    public void deleteOption(UUID optionId, UUID sectionId) {
        Option existingOption = getOption(optionId, sectionId);

        try {
            optionsRepository.delete(existingOption);
            log.info("Option deleted: {}", existingOption.getOptionId());
        } catch (Exception e) {
            log.error("Failed to delete an option: {}", e.getMessage());
            throw new DbFailure("Failed to delete an option");
        }
    }

    public Option getOption(UUID optionId, UUID sectionId) {
        return optionsRepository.findByOptionIdAndSectionId(optionId, sectionId)
                .orElseThrow(() -> {
                    log.error("Option with id {} not found", optionId);
                    return new OptionNotFound("Option with not found");
                });
    }

    public List<OptionDTO> findAllOptionDTOs(UUID sectionId) {
        List<Option> options = optionsRepository.findBySectionId(sectionId);
        List<OptionDTO> optionDTOS = new ArrayList<>();

        for (Option option : options) {
            List<BaseElementDetails> elements = elementsService.getElementsByIds(option.getOptionId());

            optionDTOS.add(new OptionDTO(
                    mapOptionDetails(option),
                    elements
            ));
        }

        optionDTOS.sort(Comparator.comparing(o -> o.getOptionDetails().order()));
        return optionDTOS;
    }

    private OptionDetails mapOptionDetails(Option option) {
        return new OptionDetails(
                option.getOptionId(),
                option.getOptionName(),
                option.getOptionOrder()
        );
    }
}
