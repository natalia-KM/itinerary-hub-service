package com.ih.itinerary_hub_service.elements.controller;

import com.ih.itinerary_hub_service.elements.exceptions.InvalidElementRequest;
import com.ih.itinerary_hub_service.elements.model.AccommodationElementDetails;
import com.ih.itinerary_hub_service.elements.model.ActivityElementDetails;
import com.ih.itinerary_hub_service.elements.model.TransportElementDetails;
import com.ih.itinerary_hub_service.elements.requests.AccommodationElementRequest;
import com.ih.itinerary_hub_service.elements.requests.ActivityElementRequest;
import com.ih.itinerary_hub_service.elements.requests.TransportElementRequest;
import com.ih.itinerary_hub_service.elements.requests.UpdateElementOrderRequest;
import com.ih.itinerary_hub_service.elements.service.ElementsService;
import com.ih.itinerary_hub_service.elements.types.AccommodationType;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.options.persistence.entity.Option;
import com.ih.itinerary_hub_service.options.service.OptionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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

    @GetMapping("sections/{sectionId}/options/{optionId}/elements/{baseElementId}/transport")
    @Operation(summary = "${elements.getTransport.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Element retrieved")})
    public TransportElementDetails getTransportElementById(
            @PathVariable UUID optionId,
            @PathVariable UUID sectionId,
            @PathVariable UUID baseElementId
    ) {
        Option option = optionsService.getOption(optionId, sectionId);
        return elementsService.getTransportElementById(option, baseElementId);
    }

    @GetMapping("sections/{sectionId}/options/{optionId}/elements/{baseElementId}/activity")
    @Operation(summary = "${elements.getActivity.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Element retrieved")})
    public ActivityElementDetails getActivityElementById(
            @PathVariable UUID optionId,
            @PathVariable UUID sectionId,
            @PathVariable UUID baseElementId
    ) {
        Option option = optionsService.getOption(optionId, sectionId);
        return elementsService.getActivityElementById(option, baseElementId);
    }

    @GetMapping("sections/{sectionId}/options/{optionId}/elements/{baseElementId}/accommodation")
    @Operation(summary = "${elements.getAccommodation.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Element retrieved")})
    public AccommodationElementDetails getAccommodationElementById(
            @PathVariable UUID optionId,
            @PathVariable UUID sectionId,
            @PathVariable UUID baseElementId,
            @RequestParam String type
    ) {
        Option option = optionsService.getOption(optionId, sectionId);

        AccommodationType accommodationType = Arrays.stream(AccommodationType.values())
                .filter(t -> t.name().equals(type))
                .findFirst()
                .orElseThrow(() -> new InvalidElementRequest("Invalid accommodation type: " + type));

        return elementsService.getAccommElementById(option, baseElementId, accommodationType);
    }

    @PutMapping("sections/{sectionId}/options/{optionId}/elements/{baseElementId}/transport")
    @Operation(summary = "${elements.updateTransport.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Element updated")})
    public TransportElementDetails updateTransportElement(
            @PathVariable UUID optionId,
            @PathVariable UUID sectionId,
            @PathVariable UUID baseElementId,
            @RequestBody TransportElementRequest request
    ) {
        Option option = optionsService.getOption(optionId, sectionId);
        return elementsService.updateTransportElement(option, baseElementId, request);
    }

    @PutMapping("sections/{sectionId}/options/{optionId}/elements/{baseElementId}/activity")
    @Operation(summary = "${elements.updateActivity.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Element updated")})
    public ActivityElementDetails updateActivityElement(
            @PathVariable UUID optionId,
            @PathVariable UUID sectionId,
            @PathVariable UUID baseElementId,
            @RequestBody ActivityElementRequest request
    ) {
        Option option = optionsService.getOption(optionId, sectionId);
        return elementsService.updateActivityElement(option, baseElementId, request);
    }

    @PutMapping("sections/{sectionId}/options/{optionId}/elements/{baseElementId}/accommodation")
    @Operation(summary = "${elements.updateAccommodation.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Element updated")})
    public List<AccommodationElementDetails> updateAccommodationElement(
            @PathVariable UUID optionId,
            @PathVariable UUID sectionId,
            @PathVariable UUID baseElementId,
            @RequestBody AccommodationElementRequest request
    ) {
        Option option = optionsService.getOption(optionId, sectionId);
        return elementsService.updateAccommodationElements(option, baseElementId, request);
    }

    @PutMapping("elements/{baseElementId}")
    @Operation(summary = "${elements.updateElementOrder.summary}", description = "${elements.updateElementOrder.desc}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Element updated")})
    public void updateOrder(
            @PathVariable UUID baseElementId,
            @Valid @RequestBody UpdateElementOrderRequest request
            ) {
        elementsService.updateElementOrder(request.order(), baseElementId, request.elementType(), request.accommodationType());
    }

    @DeleteMapping("sections/{sectionId}/options/{optionId}/elements/{baseElementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "${elements.deleteElement.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Element updated")})
    public void deleteElement(
            @PathVariable UUID optionId,
            @PathVariable UUID sectionId,
            @PathVariable UUID baseElementId,
            @RequestParam String type
    ) {
        Option option = optionsService.getOption(optionId, sectionId);

        ElementType elementType = Arrays.stream(ElementType.values())
                .filter(t -> t.name().equals(type))
                .findFirst()
                .orElseThrow(() -> new InvalidElementRequest("Invalid element type: " + type));

        elementsService.deleteElement(option, baseElementId, elementType);
    }
}
