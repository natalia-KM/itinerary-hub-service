package com.ih.itinerary_hub_service.trips.controller;

import com.ih.itinerary_hub_service.trips.requests.CreateTripRequest;
import com.ih.itinerary_hub_service.trips.requests.UpdateTripRequest;
import com.ih.itinerary_hub_service.trips.responses.TripDetails;
import com.ih.itinerary_hub_service.trips.service.TripsService;
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
@RequestMapping(value = "/v1")
@Slf4j
public class TripsController {

    private static final String TRIPS_PATH = "/trips";

    private final TripsService tripsService;

    @Autowired
    public TripsController(TripsService tripsService) {
        this.tripsService = tripsService;
    }

    @GetMapping(TRIPS_PATH)
    @Operation(summary = "${trips.getAllTrips.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "All trips retrieved")})
    public List<TripDetails> getTrips(@RequestAttribute("userId") UUID userId) {
        return tripsService.getTrips(userId);
    }

    @PostMapping(TRIPS_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "${trips.createTrip.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Trip created")})
    public void createTrip(@RequestAttribute("userId") UUID userId, @Valid @RequestBody CreateTripRequest request) {
        tripsService.createTrip(userId, request);
    }

    @GetMapping(TRIPS_PATH + "/{tripId}")
    @Operation(summary = "${trips.getTripById.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Trip retrieved")})
    public TripDetails getTripById(@RequestAttribute("userId") UUID userId, @PathVariable UUID tripId) {
        return tripsService.getTripById(userId, tripId);
    }

    @PutMapping(TRIPS_PATH  + "/{tripId}")
    @Operation(summary = "${trips.updateTrip.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Trip updated")})
    public void updateTrip(@RequestAttribute("userId") UUID userId, @PathVariable UUID tripId, @Valid @RequestBody UpdateTripRequest request) {
        tripsService.updateTrip(userId, tripId, request);
    }

    @DeleteMapping(TRIPS_PATH  + "/{tripId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "${trips.deleteTrip.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Trip deleted")})
    public void deleteTrip(@RequestAttribute("userId") UUID userId, @PathVariable UUID tripId) {
        tripsService.deleteTrip(userId, tripId);
    }
}
