package com.ih.itinerary_hub_service.sections.controller;

import com.ih.itinerary_hub_service.sections.requests.CreateSectionRequest;
import com.ih.itinerary_hub_service.sections.requests.UpdateSectionRequest;
import com.ih.itinerary_hub_service.sections.responses.SectionDetails;
import com.ih.itinerary_hub_service.sections.service.SectionService;
import com.ih.itinerary_hub_service.trips.persistence.entity.Trip;
import com.ih.itinerary_hub_service.trips.service.TripsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@Slf4j
public class SectionsController {

    private final TripsService tripsService;
    private final SectionService sectionService;

    @Autowired
    public SectionsController(TripsService tripsService, SectionService sectionService) {
        this.tripsService = tripsService;
        this.sectionService = sectionService;
    }

    @PostMapping("/trips/{tripId}/sections")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "${sections.createSection.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Section created")})
    public SectionDetails createSection(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID tripId,
            @RequestBody CreateSectionRequest createSectionRequest
    ) {
        Trip trip = tripsService.getTrip(userId, tripId);
        return sectionService.createSection(trip, createSectionRequest);
    }

    @GetMapping("/trips/{tripId}/sections/{sectionId}")
    @Operation(summary = "${sections.getSectionById.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Section retrieved")})
    public SectionDetails getSection(
            @PathVariable UUID tripId,
            @PathVariable UUID sectionId
    ) {
        return sectionService.getSectionDetails(sectionId, tripId);
    }

    @PutMapping("/trips/{tripId}/sections/{sectionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "${sections.updateSection.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Section updated")})
    public void updateSection(
            @PathVariable UUID tripId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody UpdateSectionRequest request
    ) {
        sectionService.updateSection(sectionId, tripId, request);
    }

    @DeleteMapping("/trips/{tripId}/sections/{sectionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "${sections.deleteSection.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Section deleted")})
    public void deleteSection(
            @PathVariable UUID tripId,
            @PathVariable UUID sectionId
    ) {
        sectionService.deleteSection(sectionId, tripId);
    }
}
