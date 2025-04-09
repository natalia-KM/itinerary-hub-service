package com.ih.itinerary_hub_service.options.controller;

import com.ih.itinerary_hub_service.options.requests.CreateOptionRequest;
import com.ih.itinerary_hub_service.options.requests.UpdateOptionRequest;
import com.ih.itinerary_hub_service.options.responses.OptionDetails;
import com.ih.itinerary_hub_service.options.service.OptionsService;
import com.ih.itinerary_hub_service.sections.persistence.entity.Section;
import com.ih.itinerary_hub_service.sections.service.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@Slf4j
public class OptionsController {

    private final SectionService sectionService;
    private final OptionsService optionsService;

    @Autowired
    public OptionsController(SectionService sectionService, OptionsService optionsService) {
        this.sectionService = sectionService;
        this.optionsService = optionsService;
    }

    @PostMapping("/trips/{tripId}/sections/{sectionId}/options")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "${options.createOption.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Option created")})
    public OptionDetails createOption(
            @PathVariable UUID tripId,
            @PathVariable UUID sectionId,
            @RequestBody CreateOptionRequest createOptionRequest
            ) {
        Section existingSection = sectionService.getSection(sectionId, tripId);
        return optionsService.createOption(existingSection, createOptionRequest);
    }

    @GetMapping("/sections/{sectionId}/options/{optionId}")
    @Operation(summary = "${options.getOptionById.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Option retrieved")})
    public OptionDetails getOption(
            @PathVariable UUID sectionId,
            @PathVariable UUID optionId
    ) {
        return optionsService.getOptionDetails(optionId, sectionId);
    }

    @GetMapping("/sections/{sectionId}/options")
    @Operation(summary = "${options.getOptionById.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Options retrieved")})
    public List<OptionDetails> getOptionsInSection(
            @PathVariable UUID sectionId
    ) {
        return optionsService.getOptions(sectionId);
    }

    @PutMapping("/sections/{sectionId}/options")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "${options.getOptionById.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Options updated")})
    public void updateOrder(
            @PathVariable UUID sectionId,
            @RequestBody List<OptionDetails> updatedDetails
    ) {
        optionsService.updateOptionOrders(sectionId, updatedDetails);
    }

    @PutMapping("/sections/{sectionId}/options/{optionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "${options.updateOption.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Option updated")})
    public void updateOption(
            @PathVariable UUID sectionId,
            @PathVariable UUID optionId,
            @Valid @RequestBody UpdateOptionRequest updateOptionRequest
    ) {
        optionsService.updateOption(optionId, sectionId, updateOptionRequest);
    }

    @DeleteMapping("/sections/{sectionId}/options/{optionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "${options.deleteOption.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Option deleted")})
    public void deleteOption(
            @PathVariable UUID sectionId,
            @PathVariable UUID optionId
    ) {
        optionsService.deleteOption(optionId, sectionId);
    }
}
