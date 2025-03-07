package com.ih.itinerary_hub_service.health.service;

import org.springframework.stereotype.Service;

@Service
public class HealthService {
    public String getHealth() {
        return "Itinerary Hub Service is up and running!";
    }
}
