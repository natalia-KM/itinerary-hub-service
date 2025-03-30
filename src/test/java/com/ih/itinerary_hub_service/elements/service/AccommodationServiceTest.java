package com.ih.itinerary_hub_service.elements.service;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.persistence.entity.AccommodationElement;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.persistence.repository.AccommodationElementRepository;
import com.ih.itinerary_hub_service.elements.persistence.repository.AccommodationEventRepository;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

    @Mock
    private AccommodationElementRepository accElementRepository;

    @Mock
    private AccommodationEventRepository accEventRepository;

    @InjectMocks
    private AccommodationService accommodationService;

    @Test
    void createElement_whenDbFailure_shouldThrowException() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.ACCOMMODATION);
        when(accElementRepository.save(any())).thenThrow(new IllegalArgumentException());

        assertThrows(DbFailure.class, () -> accommodationService.createElements(MockData.mockAccomRequest, baseElement));
    }

    @Test
    void createElement_whenDbFailOnEvent_shouldThrowException() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.ACCOMMODATION);
        when(accEventRepository.save(any())).thenThrow(new IllegalArgumentException());

        assertThrows(DbFailure.class, () -> accommodationService.createElements(MockData.mockAccomRequest, baseElement));
    }

    @Test
    void getElement_whenElementNotFound_shouldThrowException() {
        UUID baseElementID = UUID.randomUUID();
        BaseElement baseElement = MockData.getNewBaseElement(baseElementID, ElementType.ACCOMMODATION);
        when(accElementRepository.getAccommElementByBaseId(baseElementID)).thenReturn(Optional.empty());

        assertThrows(ElementDoesNotExist.class, () -> accommodationService.getAccommodationDetailsPair(baseElement));
    }

    @Test
    void getElement_whenEventNotFound_shouldThrowException() {
        UUID baseElementID = UUID.randomUUID();
        UUID elementID = UUID.randomUUID();

        BaseElement baseElement = MockData.getNewBaseElement(baseElementID, ElementType.ACCOMMODATION);
        AccommodationElement accElement = new AccommodationElement(elementID, baseElement, "place", "location");

        when(accElementRepository.getAccommElementByBaseId(baseElementID)).thenReturn(Optional.of(accElement));
        when(accEventRepository.getAccommodationEventsByAccommodationId(elementID)).thenReturn(List.of());

        assertThrows(ElementDoesNotExist.class, () -> accommodationService.getAccommodationDetailsPair(baseElement));
    }

}