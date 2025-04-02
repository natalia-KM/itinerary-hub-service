package com.ih.itinerary_hub_service.elements.service;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.exceptions.InvalidElementRequest;
import com.ih.itinerary_hub_service.elements.model.AccommodationElementDetails;
import com.ih.itinerary_hub_service.elements.persistence.entity.AccommodationElement;
import com.ih.itinerary_hub_service.elements.persistence.entity.AccommodationEvent;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.persistence.repository.AccommodationElementRepository;
import com.ih.itinerary_hub_service.elements.persistence.repository.AccommodationEventRepository;
import com.ih.itinerary_hub_service.elements.requests.AccommodationElementRequest;
import com.ih.itinerary_hub_service.elements.requests.AccommodationEventRequest;
import com.ih.itinerary_hub_service.elements.requests.BaseElementRequest;
import com.ih.itinerary_hub_service.elements.types.AccommodationType;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

    @Mock
    private AccommodationElementRepository accElementRepository;

    @Mock
    private AccommodationEventRepository accEventRepository;

    @InjectMocks
    private AccommodationService accommodationService;

    private final UUID randomUUID = UUID.randomUUID();

    private final BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.ACCOMMODATION);

    private final AccommodationElement element = new AccommodationElement(
            randomUUID,
            baseElement,
            "place",
            "location"
    );
    private final AccommodationEvent checkIn = new AccommodationEvent(
            UUID.randomUUID(),
            element,
            AccommodationType.CHECK_IN,
            LocalDateTime.now(),
            1
    );

    private final AccommodationEvent checkOut = new AccommodationEvent(
            UUID.randomUUID(),
            element,
            AccommodationType.CHECK_OUT,
            LocalDateTime.now().plusDays(1),
            2
    );


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

    @Test
    void updateElementOrder_whenTypeIsEmpty_shouldThrowException() {
        assertThrows(InvalidElementRequest.class, () -> accommodationService.updateElementOrder(1, randomUUID, Optional.empty()));
    }

    @Test
    void updateElementOrder_shouldThrow_whenDbFails() {
        when(accElementRepository.getAccommElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));
        when(accEventRepository.getAccommodationEventsByAccommodationIdAndType(element.getElementId(), AccommodationType.CHECK_IN)).thenReturn(Optional.of(checkIn));

        doThrow(IllegalArgumentException.class).when(accEventRepository).save(checkIn);
        assertThrows(DbFailure.class, () -> accommodationService.updateElementOrder(1, baseElement.getBaseElementId(), Optional.of(AccommodationType.CHECK_IN)));
    }

    @Test
    void updateElement_whenDbFailureOnElements_shouldThrowException() {
        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.ACCOMMODATION).build();

        AccommodationElementRequest request = AccommodationElementRequest.builder()
                .baseElementRequest(base)
                .build();

        when(accElementRepository.getAccommElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));

        doThrow(IllegalArgumentException.class).when(accElementRepository).save(element);
        assertThrows(DbFailure.class, () -> accommodationService.updateAccommodationElements(request, baseElement));
    }

    @Test
    void updateElement_whenDbFailureOnEvents_shouldThrowException() {
        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.ACCOMMODATION).build();

        AccommodationElementRequest request = AccommodationElementRequest.builder()
                .baseElementRequest(base)
                .build();

        when(accElementRepository.getAccommElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));
        when(accEventRepository.getAccommodationEventsByAccommodationIdAndType(randomUUID, AccommodationType.CHECK_IN)).thenReturn(Optional.of(checkIn));
        when(accEventRepository.getAccommodationEventsByAccommodationIdAndType(randomUUID, AccommodationType.CHECK_OUT)).thenReturn(Optional.of(checkOut));

        doThrow(IllegalArgumentException.class).when(accEventRepository).save(checkIn);
        assertThrows(DbFailure.class, () -> accommodationService.updateAccommodationElements(request, baseElement));
    }

    @Test
    void updateElement_whenElementsAreNull_doNotReplaceOriginal() {
        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.ACCOMMODATION).build();

        AccommodationElementRequest request = AccommodationElementRequest.builder()
                .baseElementRequest(base)
                .build();

        when(accElementRepository.getAccommElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));
        when(accEventRepository.getAccommodationEventsByAccommodationIdAndType(randomUUID, AccommodationType.CHECK_IN)).thenReturn(Optional.of(checkIn));
        when(accEventRepository.getAccommodationEventsByAccommodationIdAndType(randomUUID, AccommodationType.CHECK_OUT)).thenReturn(Optional.of(checkOut));

        List<AccommodationElementDetails> result = accommodationService.updateAccommodationElements(request, baseElement);

        assertNotNull(result);
        assertEquals(element.getPlace(), result.get(0).getPlace());
        assertEquals(element.getLocation(), result.get(0).getLocation());

        assertEquals(checkIn.getDatetime(), result.get(0).getDateTime());
        assertEquals(checkIn.getElementOrder(), result.get(0).getOrder());

        assertEquals(checkOut.getDatetime(), result.get(1).getDateTime());
        assertEquals(checkOut.getElementOrder(), result.get(1).getOrder());
    }

    @Test
    void updateElement_whenFieldsAreValid_replaceOriginal() {
        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.ACCOMMODATION).build();

        AccommodationEventRequest checkInRequest = new AccommodationEventRequest(
                LocalDateTime.now().plusDays(5),
                5
        );

        AccommodationEventRequest checkOutRequest = new AccommodationEventRequest(
                LocalDateTime.now().plusDays(5),
                5
        );
        AccommodationElementRequest request = AccommodationElementRequest.builder()
                .baseElementRequest(base)
                .place("new place")
                .location("new location")
                .checkIn(checkInRequest)
                .checkOut(checkOutRequest)
                .build();

        when(accElementRepository.getAccommElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));
        when(accEventRepository.getAccommodationEventsByAccommodationIdAndType(randomUUID, AccommodationType.CHECK_IN)).thenReturn(Optional.of(checkIn));
        when(accEventRepository.getAccommodationEventsByAccommodationIdAndType(randomUUID, AccommodationType.CHECK_OUT)).thenReturn(Optional.of(checkOut));

        List<AccommodationElementDetails> result = accommodationService.updateAccommodationElements(request, baseElement);

        assertNotNull(result);
        assertEquals(request.getPlace(), result.get(0).getPlace());
        assertEquals(request.getLocation(), result.get(0).getLocation());

        assertEquals(checkInRequest.getDateTime(), result.get(0).getDateTime());
        assertEquals(checkInRequest.getOrder(), result.get(0).getOrder());

        assertEquals(checkOutRequest.getDateTime(), result.get(1).getDateTime());
        assertEquals(checkOutRequest.getOrder(), result.get(1).getOrder());
    }

    @Test
    void updateElement_whenFieldsAreBlankOrNull() {
        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.ACCOMMODATION).build();

        AccommodationEventRequest checkInRequest = new AccommodationEventRequest(
                null,
                null
        );

        AccommodationEventRequest checkOutRequest = new AccommodationEventRequest(
                null,
                null
        );
        AccommodationElementRequest request = AccommodationElementRequest.builder()
                .baseElementRequest(base)
                .place(" ")
                .location(" ")
                .checkIn(checkInRequest)
                .checkOut(checkOutRequest)
                .build();

        when(accElementRepository.getAccommElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));
        when(accEventRepository.getAccommodationEventsByAccommodationIdAndType(randomUUID, AccommodationType.CHECK_IN)).thenReturn(Optional.of(checkIn));
        when(accEventRepository.getAccommodationEventsByAccommodationIdAndType(randomUUID, AccommodationType.CHECK_OUT)).thenReturn(Optional.of(checkOut));

        List<AccommodationElementDetails> result = accommodationService.updateAccommodationElements(request, baseElement);

        assertNotNull(result);
        assertEquals(element.getPlace(), result.get(0).getPlace());

        assertNull(result.get(0).getLocation()); // location is not a required field so blank makes it null

        assertEquals(checkIn.getDatetime(), result.get(0).getDateTime());
        assertEquals(checkIn.getElementOrder(), result.get(0).getOrder());

        assertEquals(checkOut.getDatetime(), result.get(1).getDateTime());
        assertEquals(checkOut.getElementOrder(), result.get(1).getOrder());
    }

    @Test
    void deleteElement_whenDbFails_throwException() {
        List<AccommodationEvent> events = Arrays.asList(checkIn, checkOut);

        when(accElementRepository.getAccommElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));
        when(accEventRepository.getAccommodationEventsByAccommodationId(element.getElementId())).thenReturn(events);

        doThrow(IllegalArgumentException.class).when(accEventRepository).deleteAll(any());
        assertThrows(DbFailure.class, () -> accommodationService.deleteElement(baseElement.getBaseElementId()));
    }

    @Test
    void getSingleAccommodationDetailsElement_whenDoesNotExist_throwException() {
        when(accElementRepository.getAccommElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));
        when(accEventRepository.getAccommodationEventsByAccommodationIdAndType(randomUUID, AccommodationType.CHECK_IN)).thenReturn(Optional.empty());

        assertThrows(ElementDoesNotExist.class, () -> accommodationService.getSingleAccommodationDetailsElement(baseElement, AccommodationType.CHECK_IN));
    }

    @Test
    void getSingleAccommodationDetailsElement_whenTypeDoesNotMatch_throwException() {
        when(accElementRepository.getAccommElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));
        when(accEventRepository.getAccommodationEventsByAccommodationIdAndType(randomUUID, AccommodationType.CHECK_IN)).thenReturn(Optional.of(checkOut));

        assertThrows(ElementDoesNotExist.class, () -> accommodationService.getSingleAccommodationDetailsElement(baseElement, AccommodationType.CHECK_IN));
    }
}