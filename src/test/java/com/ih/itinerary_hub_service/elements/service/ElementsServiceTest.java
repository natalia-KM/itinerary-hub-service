package com.ih.itinerary_hub_service.elements.service;

import com.ih.itinerary_hub_service.elements.exceptions.InvalidElementRequest;
import com.ih.itinerary_hub_service.elements.model.BaseElementDetails;
import com.ih.itinerary_hub_service.elements.model.TransportElementDetails;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.persistence.repository.BaseElementRepository;
import com.ih.itinerary_hub_service.elements.requests.*;
import com.ih.itinerary_hub_service.elements.types.AccommodationType;
import com.ih.itinerary_hub_service.elements.types.ElementStatus;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.utils.MockData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.ih.itinerary_hub_service.utils.MockData.getNewBaseElement;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElementsServiceTest {

    @Mock
    private BaseElementRepository baseElementRepository;

    @Mock
    private TransportService transportService;

    @InjectMocks
    private ElementsService elementsService;

    private final UUID elementId = UUID.randomUUID();

    @Nested
    class Create {
        @ParameterizedTest
        @MethodSource("invalidRequestValuesTransport")
        void shouldThrowExceptionWhenCreateRequestIsInvalid_transport(Integer order, ElementType type) {
            BaseElementRequest base = new BaseElementRequest(
                    type,
                    null,
                    null,
                    BigDecimal.valueOf(23.45),
                    "Notes",
                    ElementStatus.PENDING
            );

            TransportElementRequest request = TransportElementRequest.builder()
                    .baseElementRequest(base)
                    .originPlace("origin")
                    .destinationPlace("destination")
                    .originDateTime(LocalDateTime.now())
                    .destinationDateTime(LocalDateTime.now().plusDays(1))
                    .order(order)
                    .build();

            assertThrows(InvalidElementRequest.class, () -> elementsService.createTransportElement(MockData.mockOption, request));
        }

        private static Stream<Arguments> invalidRequestValuesTransport() {
            return Stream.of(
                    Arguments.of(null, ElementType.TRANSPORT),
                    Arguments.of(1, ElementType.ACTIVITY)
            );
        }

        @ParameterizedTest
        @MethodSource("invalidRequestValuesActivity")
        void shouldThrowExceptionWhenRequestIsInvalid_activity(Integer order, ElementType type) {
            BaseElementRequest base = new BaseElementRequest(
                    type,
                    null,
                    null,
                    BigDecimal.valueOf(23.45),
                    "Notes",
                    ElementStatus.PENDING
            );

            ActivityElementRequest request = ActivityElementRequest.builder()
                    .baseElementRequest(base)
                    .activityName("activity")
                    .location("location")
                    .startsAt(LocalDateTime.now())
                    .duration(null)
                    .order(order)
                    .build();

            assertThrows(InvalidElementRequest.class, () -> elementsService.createActivityElement(MockData.mockOption, request));
        }

        private static Stream<Arguments> invalidRequestValuesActivity() {
            return Stream.of(
                    Arguments.of(null, ElementType.ACTIVITY),
                    Arguments.of(1, ElementType.ACCOMMODATION)
            );
        }

        @ParameterizedTest
        @MethodSource("invalidRequestValuesAccom")
        void shouldThrowExceptionWhenRequestIsInvalid_accomm(Integer checkInOrder, Integer checkOutOrder, ElementType type) {
            BaseElementRequest base = new BaseElementRequest(
                    type,
                    null,
                    null,
                    BigDecimal.valueOf(23.45),
                    "Notes",
                    ElementStatus.PENDING
            );
            AccommodationEventRequest checkIn = new AccommodationEventRequest(LocalDateTime.now(), checkInOrder);
            AccommodationEventRequest checkOut = new AccommodationEventRequest(LocalDateTime.now().plusDays(1), checkOutOrder);

            AccommodationElementRequest request = AccommodationElementRequest.builder()
                    .baseElementRequest(base)
                    .place("place")
                    .location("location")
                    .checkIn(checkIn)
                    .checkOut(checkOut)
                    .build();

            assertThrows(InvalidElementRequest.class, () -> elementsService.createAccommodationsElement(MockData.mockOption, request));
        }

        private static Stream<Arguments> invalidRequestValuesAccom() {
            return Stream.of(
                    Arguments.of(null, 2, ElementType.ACCOMMODATION),
                    Arguments.of(1, null, ElementType.ACCOMMODATION),
                    Arguments.of(1, 2, ElementType.TRANSPORT)
            );
        }

        @Test
        void shouldThrow_whenDbFails() {
            BaseElementRequest base = BaseElementRequest.builder()
                    .elementType(ElementType.ACTIVITY)
                    .build();

            ActivityElementRequest request = ActivityElementRequest.builder()
                    .baseElementRequest(base)
                    .activityName("activity")
                    .location("location")
                    .order(1)
                    .build();

            doThrow(IllegalArgumentException.class).when(baseElementRepository).save(any());

            assertThrows(DbFailure.class, () -> elementsService.createActivityElement(MockData.mockOption, request));
        }
    }


    @Nested
    class Get {
        @Test
        void shouldThrowExceptionWhenGetRequestIsInvalid_transport() {
            ElementType invalidType = ElementType.ACTIVITY;

            BaseElement baseElement = MockData.getNewBaseElement(elementId, invalidType);
            when(baseElementRepository.findByBaseIdAndOptionId(elementId, MockData.optionId)).thenReturn(Optional.of(baseElement));

            assertThrows(InvalidElementRequest.class, () -> elementsService.getTransportElementById(MockData.mockOption, elementId));
        }

        @Test
        void shouldThrowExceptionWhenGetRequestIsInvalid_activity() {
            ElementType invalidType = ElementType.ACCOMMODATION;

            BaseElement baseElement = MockData.getNewBaseElement(elementId, invalidType);
            when(baseElementRepository.findByBaseIdAndOptionId(elementId, MockData.optionId)).thenReturn(Optional.of(baseElement));

            assertThrows(InvalidElementRequest.class, () -> elementsService.getActivityElementById(MockData.mockOption, elementId));
        }

        @Test
        void shouldThrowExceptionWhenGetRequestIsInvalid_accomm() {
            ElementType invalidType = ElementType.TRANSPORT;

            BaseElement baseElement = MockData.getNewBaseElement(elementId, invalidType);
            when(baseElementRepository.findByBaseIdAndOptionId(elementId, MockData.optionId)).thenReturn(Optional.of(baseElement));

            assertThrows(InvalidElementRequest.class, () -> elementsService.getAccommElementById(MockData.mockOption, elementId, AccommodationType.CHECK_IN));
        }
    }

    @Nested
    class Update {

        @Test
        void shouldThrowExceptionWhenUpdateRequestIsInvalid_transport() {
            ElementType invalidType = ElementType.ACTIVITY;

            BaseElementRequest base = new BaseElementRequest(
                    invalidType,
                    null,
                    null,
                    BigDecimal.valueOf(23.45),
                    "Notes",
                    ElementStatus.PENDING
            );

            TransportElementRequest request = TransportElementRequest.builder()
                    .baseElementRequest(base)
                    .originPlace("origin")
                    .destinationPlace("destination")
                    .originDateTime(LocalDateTime.now())
                    .destinationDateTime(LocalDateTime.now().plusDays(1))
                    .build();

            BaseElement baseElement = MockData.getNewBaseElement(elementId, invalidType);
            when(baseElementRepository.findByBaseIdAndOptionId(elementId, MockData.optionId)).thenReturn(Optional.of(baseElement));

            assertThrows(InvalidElementRequest.class, () -> elementsService.updateTransportElement(MockData.mockOption, elementId, request));
        }

        @Test
        void shouldThrowExceptionWhenUpdateRequestIsInvalid_activity() {
            ElementType invalidType = ElementType.ACCOMMODATION;

            BaseElementRequest base = new BaseElementRequest(
                    invalidType,
                    null,
                    null,
                    BigDecimal.valueOf(23.45),
                    "Notes",
                    ElementStatus.PENDING
            );

            ActivityElementRequest request = ActivityElementRequest.builder()
                    .baseElementRequest(base)
                    .activityName("activity")
                    .location("location")
                    .startsAt(LocalDateTime.now())
                    .build();

            BaseElement baseElement = MockData.getNewBaseElement(elementId, invalidType);
            when(baseElementRepository.findByBaseIdAndOptionId(elementId, MockData.optionId)).thenReturn(Optional.of(baseElement));

            assertThrows(InvalidElementRequest.class, () -> elementsService.updateActivityElement(MockData.mockOption, elementId, request));
        }

        @Test
        void shouldThrowExceptionWhenUpdateRequestIsInvalid_accomm() {
            ElementType invalidType = ElementType.ACTIVITY;

            BaseElementRequest base = new BaseElementRequest(
                    invalidType,
                    null,
                    null,
                    BigDecimal.valueOf(23.45),
                    "Notes",
                    ElementStatus.PENDING
            );
            AccommodationEventRequest checkIn = new AccommodationEventRequest(LocalDateTime.now(), null);
            AccommodationEventRequest checkOut = new AccommodationEventRequest(LocalDateTime.now().plusDays(1), null);

            AccommodationElementRequest request = AccommodationElementRequest.builder()
                    .baseElementRequest(base)
                    .place("place")
                    .location("location")
                    .checkIn(checkIn)
                    .checkOut(checkOut)
                    .build();
            BaseElement baseElement = MockData.getNewBaseElement(elementId, invalidType);
            when(baseElementRepository.findByBaseIdAndOptionId(elementId, MockData.optionId)).thenReturn(Optional.of(baseElement));

            assertThrows(InvalidElementRequest.class, () -> elementsService.updateAccommodationElements(MockData.mockOption, elementId, request));
        }


        @Test
        void shouldThrow_whenDbFails() {
            BaseElement baseElement = MockData.getNewBaseElement(elementId, ElementType.ACTIVITY);

            BaseElementRequest base = BaseElementRequest.builder()
                    .build();

            ActivityElementRequest request = ActivityElementRequest.builder()
                    .baseElementRequest(base)
                    .build();

            when(baseElementRepository.findByBaseIdAndOptionId(elementId, MockData.optionId)).thenReturn(Optional.of(baseElement));
            doThrow(IllegalArgumentException.class).when(baseElementRepository).save(baseElement);

            assertThrows(DbFailure.class, () -> elementsService.updateActivityElement(MockData.mockOption, elementId, request));
        }
    }

    @Nested
    class Delete {
        @Test
        void shouldThrow_whenDbFails() {
            BaseElement baseElement = MockData.getNewBaseElement(elementId, ElementType.TRANSPORT);

            when(baseElementRepository.findByBaseIdAndOptionId(elementId, MockData.optionId)).thenReturn(Optional.of(baseElement));
            doThrow(IllegalArgumentException.class).when(baseElementRepository).delete(baseElement);

            assertThrows(DbFailure.class, () -> elementsService.deleteElement(MockData.mockOption, baseElement.getBaseElementId(), ElementType.TRANSPORT));
        }
    }

    @Test
    void shouldSortElements() {
        BaseElement baseElement1 = getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);
        BaseElement baseElement2 = getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);
        BaseElement baseElement3 = getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);
        BaseElement baseElement4 = getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);
        BaseElement baseElement5 = getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);
        List<BaseElement> baseElements = Arrays.asList(baseElement1, baseElement2, baseElement3, baseElement4, baseElement5);

        TransportElementDetails tr1 = getNewTransportElementDetails(baseElement1, 5);
        TransportElementDetails tr2 = getNewTransportElementDetails(baseElement2, 4);
        TransportElementDetails tr3 = getNewTransportElementDetails(baseElement3, 1);
        TransportElementDetails tr4 = getNewTransportElementDetails(baseElement4, 3);
        TransportElementDetails tr5 = getNewTransportElementDetails(baseElement5, 2);

        when(baseElementRepository.findByOptionId(MockData.optionId)).thenReturn(baseElements);

        when(transportService.getTransportElementDetails(baseElement1)).thenReturn(tr1);
        when(transportService.getTransportElementDetails(baseElement2)).thenReturn(tr2);
        when(transportService.getTransportElementDetails(baseElement3)).thenReturn(tr3);
        when(transportService.getTransportElementDetails(baseElement4)).thenReturn(tr4);
        when(transportService.getTransportElementDetails(baseElement5)).thenReturn(tr5);

        List<BaseElementDetails> result = elementsService.getElementsByIds(MockData.optionId);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(baseElement3.getBaseElementId(), result.get(0).getBaseElementID());
        assertEquals(baseElement5.getBaseElementId(), result.get(1).getBaseElementID());
        assertEquals(baseElement4.getBaseElementId(), result.get(2).getBaseElementID());
        assertEquals(baseElement2.getBaseElementId(), result.get(3).getBaseElementID());
        assertEquals(baseElement1.getBaseElementId(), result.get(4).getBaseElementID());
    }

    private static TransportElementDetails getNewTransportElementDetails(BaseElement baseElement, Integer order) {
        TransportElementDetails.Builder builder = new TransportElementDetails.Builder();
        return builder
                .baseElementID(baseElement.getBaseElementId())
                .optionID(baseElement.getOption().getOptionId())
                .lastUpdatedAt(LocalDateTime.now())
                .elementType(baseElement.getElementType())
                .order(order)
                .originPlace("Origin")
                .destinationPlace("Destination")
                .originDateTime(LocalDateTime.now())
                .destinationDateTime(LocalDateTime.now().plusDays(1))
                .originProvider(null)
                .destinationProvider(null)
                .build();
    }
}