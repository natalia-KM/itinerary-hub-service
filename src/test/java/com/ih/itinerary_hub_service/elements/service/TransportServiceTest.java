package com.ih.itinerary_hub_service.elements.service;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.persistence.repository.TransportRepository;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransportServiceTest {

    @Mock
    private TransportRepository transportRepository;

    @InjectMocks
    private TransportService transportService;

    @Test
    void createElement_whenDbFail_shouldThrowException() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);
        when(transportRepository.save(any())).thenThrow(new IllegalArgumentException());

        assertThrows(DbFailure.class, () -> transportService.createElement(MockData.mockTransportRequest, baseElement));
    }

    @Test
    void getElement_whenElementNotFound_shouldThrowException() {
        UUID baseElementID = UUID.randomUUID();
        when(transportRepository.getTransportElementByBaseId(baseElementID)).thenReturn(Optional.empty());

        assertThrows(ElementDoesNotExist.class, () -> transportService.getElementByBaseId(baseElementID));
    }
}