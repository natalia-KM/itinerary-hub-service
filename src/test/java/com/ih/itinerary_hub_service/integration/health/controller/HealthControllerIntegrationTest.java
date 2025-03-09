package com.ih.itinerary_hub_service.integration.health.controller;

import com.ih.itinerary_hub_service.health.service.HealthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;

@IntegrationTestConfig
class HealthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HealthService healthService;

    @Test
    void health_whenServiceIsOk_thenReturn200() throws Exception {
        when(healthService.getHealth()).thenReturn("OK");

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/health"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().string("OK"));
        verify(healthService, times(1)).getHealth();
    }

    @Test
    void health_returns500_whenServiceFails() throws Exception {
        when(healthService.getHealth()).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/health"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

}