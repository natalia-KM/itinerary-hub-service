package com.ih.itinerary_hub_service.unit.health.service;

import com.ih.itinerary_hub_service.health.service.HealthService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HealthServiceTest {

    @Test
    void getHealth_shouldReturnHealthMessage() {
        HealthService healthService = new HealthService();
        String healthMessage = healthService.getHealth();
        assertEquals("Itinerary Hub Service is up and running!", healthMessage);
    }

}