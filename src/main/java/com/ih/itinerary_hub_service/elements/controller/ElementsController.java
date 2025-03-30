package com.ih.itinerary_hub_service.elements.controller;

import com.ih.itinerary_hub_service.elements.model.AccommodationElementDetails;
import com.ih.itinerary_hub_service.elements.model.ActivityElementDetails;
import com.ih.itinerary_hub_service.elements.model.TransportElementDetails;
import com.ih.itinerary_hub_service.elements.requests.AccommodationElementRequest;
import com.ih.itinerary_hub_service.elements.requests.ActivityElementRequest;
import com.ih.itinerary_hub_service.elements.requests.TransportElementRequest;
import com.ih.itinerary_hub_service.elements.service.ElementsService;
import com.ih.itinerary_hub_service.options.persistence.entity.Option;
import com.ih.itinerary_hub_service.options.service.OptionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@Slf4j
public class ElementsController {

    private final ElementsService elementsService;
    private final OptionsService optionsService;

    @Autowired
    public ElementsController(ElementsService elementsService, OptionsService optionsService) {
        this.elementsService = elementsService;
        this.optionsService = optionsService;
    }

    @PostMapping("sections/{sectionId}/options/{optionId}/elements/transport")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "${elements.createTransport.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Element created")})
    public TransportElementDetails createTransportElement(
            @PathVariable UUID optionId,
            @PathVariable UUID sectionId,
            @RequestBody TransportElementRequest request
    ) {
        Option option = optionsService.getOption(optionId, sectionId);
        return elementsService.createTransportElement(option, request);
    }

    @PostMapping("sections/{sectionId}/options/{optionId}/elements/activity")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "${elements.createActivity.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Element created")})
    public ActivityElementDetails createActivityElement(
            @PathVariable UUID optionId,
            @PathVariable UUID sectionId,
            @RequestBody ActivityElementRequest request
    ) {
        Option option = optionsService.getOption(optionId, sectionId);
        return elementsService.createActivityElement(option, request);
    }

    @PostMapping("sections/{sectionId}/options/{optionId}/elements/accommodation")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "${elements.createAccommodation.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Element created")})
    public List<AccommodationElementDetails> createAccommodationElement(
            @PathVariable UUID optionId,
            @PathVariable UUID sectionId,
            @RequestBody AccommodationElementRequest request
    ) {
        Option option = optionsService.getOption(optionId, sectionId);
        return elementsService.createAccommodationsElement(option, request);
    }
}
