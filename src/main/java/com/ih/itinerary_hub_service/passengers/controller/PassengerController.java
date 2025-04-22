package com.ih.itinerary_hub_service.passengers.controller;

import com.ih.itinerary_hub_service.passengers.requests.CreatePassengerRequest;
import com.ih.itinerary_hub_service.passengers.requests.PassengerRequest;
import com.ih.itinerary_hub_service.passengers.responses.PassengerDetails;
import com.ih.itinerary_hub_service.passengers.service.GlobalPassengersService;
import com.ih.itinerary_hub_service.users.persistence.entity.User;
import com.ih.itinerary_hub_service.users.service.UserService;
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
public class PassengerController {

    private static final String PASSENGERS_PATH = "/passengers";

    private final GlobalPassengersService passengersService;
    private final UserService userService;

    @Autowired
    public PassengerController(GlobalPassengersService passengersService, UserService userService) {
        this.passengersService = passengersService;
        this.userService = userService;
    }

    @GetMapping(PASSENGERS_PATH)
    @Operation(summary = "${passengers.getAll.summary}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "All passengers retrieved")})
    public List<PassengerDetails> getPassengers(@RequestAttribute("userId") UUID userId) {
        return passengersService.getAllPassengersInAccount(userId);
    }

    @GetMapping(PASSENGERS_PATH + "/{passengerId}")
    @Operation(summary = "${passengers.getById.summary}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Passenger retrieved")})
    public PassengerDetails getPassengerDetails(@PathVariable("passengerId") UUID passengerId) {
        return passengersService.getPassengerDetails(passengerId);
    }

    @PostMapping(PASSENGERS_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "${passengers.createPassenger.summary}")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Passenger created")})
    public PassengerDetails createPassenger(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody CreatePassengerRequest request
    ) {
        User user = userService.getUserById(userId);
        return passengersService.createPassenger(user, request);
    }

    @PutMapping(PASSENGERS_PATH + "/{passengerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "${passengers.updatePassenger.summary}")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Passenger updated")})
    public void updatePassengerDetails(
            @PathVariable("passengerId") UUID passengerId,
            @RequestBody PassengerRequest request
    ) {
        passengersService.updatePassenger(passengerId, request);
    }

    @DeleteMapping(PASSENGERS_PATH + "/{passengerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "${passengers.deletePassenger.summary}")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Passenger deleted")})
    public void deletePassenger(
            @PathVariable("passengerId") UUID passengerId
    ) {
        passengersService.deletePassenger(passengerId);
    }
}
