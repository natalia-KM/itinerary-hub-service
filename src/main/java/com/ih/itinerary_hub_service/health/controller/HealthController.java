package com.ih.itinerary_hub_service.health.controller;

import com.ih.itinerary_hub_service.health.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1")
public class HealthController {

    private static final String HEALTH_ENDPOINT = "/health";

    private final HealthService healthService;

    @Autowired
    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping(value = HEALTH_ENDPOINT)
    @Operation(summary = "${health.getStatus.summary}", description = "${health.getStatus.desc}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Returns health status")})
    public String getHealth() {
        return healthService.getHealth();
    }
}
