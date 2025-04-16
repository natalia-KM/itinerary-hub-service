package com.ih.itinerary_hub_service.elements.service;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.model.TransportElementDetails;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.persistence.entity.TransportElement;
import com.ih.itinerary_hub_service.elements.persistence.repository.TransportRepository;
import com.ih.itinerary_hub_service.elements.requests.BaseElementRequest;
import com.ih.itinerary_hub_service.elements.requests.TransportElementRequest;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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

        assertThrows(DbFailure.class, () -> transportService.createElement(MockData.mockTransportRequest, baseElement, List.of()));
    }

    @Test
    void updateElementOrder_whenDbFail_shouldThrowException() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);

        TransportElement transportElement = new TransportElement(
                UUID.randomUUID(),
                baseElement,
                "origin",
                LocalDateTime.now(),
                "dest",
                LocalDateTime.now().plusDays(1),
                null,
                null,
                1
        );
        when(transportRepository.getTransportElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(transportElement));
        when(transportRepository.save(any())).thenThrow(new IllegalArgumentException());

        assertThrows(DbFailure.class, () -> transportService.updateElementOrder(1, baseElement.getBaseElementId()));
    }

    @Test
    void updateElement_whenElementsAreNull_doNotReplaceOriginal() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);

        TransportElement transportElement = new TransportElement(
                UUID.randomUUID(),
                baseElement,
                "origin",
                LocalDateTime.now(),
                "dest",
                LocalDateTime.now().plusDays(1),
                "Ryanair",
                "WizzAir",
                1
        );

        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.TRANSPORT).build();

        TransportElementRequest request = TransportElementRequest.builder()
                .baseElementRequest(base)
                .build();

        when(transportRepository.getTransportElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(transportElement));

        TransportElementDetails result = transportService.updateElement(request, baseElement, List.of());

        assertNotNull(result);
        assertEquals(transportElement.getOriginPlace(), result.getOriginPlace());
        assertEquals(transportElement.getDestinationPlace(), result.getDestinationPlace());
        assertEquals(transportElement.getOriginProvider(), result.getOriginProvider());
        assertEquals(transportElement.getDestinationProvider(), result.getDestinationProvider());
        assertEquals(transportElement.getOriginDateTime(), result.getOriginDateTime());
        assertEquals(transportElement.getDestinationDateTime(), result.getDestinationDateTime());
        assertEquals(transportElement.getElementOrder(), result.getOrder());
    }

    @Test
    void updateElement_whenRequiredFieldsAreBlank_doNotReplaceOriginal() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);

        TransportElement transportElement = new TransportElement(
                UUID.randomUUID(),
                baseElement,
                "origin",
                LocalDateTime.now(),
                "dest",
                LocalDateTime.now().plusDays(1),
                "provider",
                null,
                1
        );

        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.TRANSPORT).build();

        TransportElementRequest request = TransportElementRequest.builder()
                .baseElementRequest(base)
                .originPlace(" ")
                .destinationPlace(" ")
                .build();

        when(transportRepository.getTransportElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(transportElement));

        TransportElementDetails result = transportService.updateElement(request, baseElement, List.of());

        assertNotNull(result);
        assertEquals(transportElement.getOriginPlace(), result.getOriginPlace());
        assertEquals(transportElement.getDestinationPlace(), result.getDestinationPlace());
    }

    @Test
    void updateElement_whenNonRequiredFieldsAreBlank_replaceOriginal() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);

        TransportElement transportElement = new TransportElement(
                UUID.randomUUID(),
                baseElement,
                "origin",
                LocalDateTime.now(),
                "dest",
                LocalDateTime.now().plusDays(1),
                "provider",
                null,
                1
        );

        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.TRANSPORT).build();

        TransportElementRequest request = TransportElementRequest.builder()
                .baseElementRequest(base)
                .originProvider(" ")
                .build();

        when(transportRepository.getTransportElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(transportElement));

        TransportElementDetails result = transportService.updateElement(request, baseElement, List.of());

        assertNotNull(result);
        assertNull(transportElement.getOriginProvider());
    }

    @Test
    void updateElement_whenRequiredFieldsAreValid_replaceOriginal() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);

        TransportElement transportElement = new TransportElement(
                UUID.randomUUID(),
                baseElement,
                "origin",
                LocalDateTime.now(),
                "dest",
                LocalDateTime.now().plusDays(1),
                "provider",
                null,
                1
        );

        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.TRANSPORT).build();

        TransportElementRequest request = TransportElementRequest.builder()
                .baseElementRequest(base)
                .originPlace("new origin")
                .destinationPlace("new destination")
                .originDateTime(LocalDateTime.now().plusDays(5))
                .destinationDateTime(LocalDateTime.now().plusDays(10))
                .originProvider("origin provider")
                .destinationProvider("dest provider")
                .order(2)
                .build();

        when(transportRepository.getTransportElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(transportElement));

        TransportElementDetails result = transportService.updateElement(request, baseElement, List.of());

        assertNotNull(result);
        assertEquals(request.getOriginPlace(), result.getOriginPlace());
        assertEquals(request.getDestinationPlace(), result.getDestinationPlace());
        assertEquals(request.getOriginProvider(), result.getOriginProvider());
        assertEquals(request.getDestinationProvider(), result.getDestinationProvider());
        assertEquals(request.getOriginDateTime(), result.getOriginDateTime());
        assertEquals(request.getDestinationDateTime(), result.getDestinationDateTime());
        assertEquals(request.getOrder(), result.getOrder());
    }

    @Test
    void updateElement_whenDbFails_thenThrowException() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);

        TransportElement transportElement = new TransportElement(
                UUID.randomUUID(),
                baseElement,
                "origin",
                LocalDateTime.now(),
                "dest",
                LocalDateTime.now().plusDays(1),
                "provider",
                null,
                1
        );

        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.TRANSPORT).build();

        TransportElementRequest request = TransportElementRequest.builder()
                .baseElementRequest(base)
                .build();

        when(transportRepository.getTransportElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(transportElement));
        doThrow(IllegalArgumentException.class).when(transportRepository).save(any());

        assertThrows(DbFailure.class, () -> transportService.updateElement(request, baseElement, List.of()));
    }

    @Test
    void deleteElement_whenDbFails_thenThrowException() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);

        TransportElement transportElement = new TransportElement(
                UUID.randomUUID(),
                baseElement,
                "origin",
                LocalDateTime.now(),
                "dest",
                LocalDateTime.now().plusDays(1),
                "provider",
                null,
                1
        );

        when(transportRepository.getTransportElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(transportElement));
        doThrow(IllegalArgumentException.class).when(transportRepository).delete(any());

        assertThrows(DbFailure.class, () -> transportService.deleteElement(baseElement.getBaseElementId()));
    }

    @Test
    void getElement_whenElementNotFound_shouldThrowException() {
        UUID baseElementID = UUID.randomUUID();
        when(transportRepository.getTransportElementByBaseId(baseElementID)).thenReturn(Optional.empty());

        assertThrows(ElementDoesNotExist.class, () -> transportService.getElementByBaseId(baseElementID));
    }
}