package com.ih.itinerary_hub_service.elements.service;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.model.ActivityElementDetails;
import com.ih.itinerary_hub_service.elements.persistence.entity.ActivityElement;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.persistence.repository.ActivityRepository;
import com.ih.itinerary_hub_service.elements.requests.ActivityElementRequest;
import com.ih.itinerary_hub_service.elements.requests.BaseElementRequest;
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
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    @Test
    void createActivity_whenDbFail_shouldThrowException() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.ACTIVITY);
        when(activityRepository.save(any())).thenThrow(new IllegalArgumentException());

        assertThrows(DbFailure.class, () -> activityService.createElement(MockData.mockActivityRequest, baseElement, List.of()));
    }

    @Test
    void getElement_whenElementNotFound_shouldThrowException() {
        UUID baseElementID = UUID.randomUUID();
        when(activityRepository.getActivityElementByBaseId(baseElementID)).thenReturn(Optional.empty());

        assertThrows(ElementDoesNotExist.class, () -> activityService.getElementById(baseElementID));
    }

    @Test
    void updateElementOrder_whenDbFail_shouldThrowException() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.ACTIVITY);

        ActivityElement element = new ActivityElement(
                UUID.randomUUID(),
                baseElement,
                "activity",
                "location",
                LocalDateTime.now().plusDays(1),
                120,
                1
        );
        when(activityRepository.getActivityElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));
        when(activityRepository.save(any())).thenThrow(new IllegalArgumentException());

        assertThrows(DbFailure.class, () -> activityService.updateElementOrder(1, baseElement.getBaseElementId()));
    }

    @Test
    void updateElement_whenElementsAreNull_doNotReplaceOriginal() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.ACTIVITY);

        ActivityElement element = new ActivityElement(
                UUID.randomUUID(),
                baseElement,
                "activity",
                "location",
                LocalDateTime.now().plusDays(1),
                120,
                1
        );

        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.ACTIVITY).build();

        ActivityElementRequest request = ActivityElementRequest.builder()
                .baseElementRequest(base)
                .build();

        when(activityRepository.getActivityElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));

        ActivityElementDetails result = activityService.updateElement(request, baseElement, List.of());

        assertNotNull(result);
        assertEquals(element.getActivityName(), result.getActivityName());
        assertEquals(element.getLocation(), result.getLocation());
        assertEquals(element.getStartsAt(), result.getStartsAt());
        assertEquals(element.getDuration(), result.getDuration());
        assertEquals(element.getElementOrder(), result.getOrder());
    }

    @Test
    void updateElement_whenRequiredFieldsAreBlank_doNotReplaceOriginal() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.ACTIVITY);

        ActivityElement element = new ActivityElement(
                UUID.randomUUID(),
                baseElement,
                "activity",
                "location",
                LocalDateTime.now().plusDays(1),
                120,
                1
        );

        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.ACTIVITY).build();

        ActivityElementRequest request = ActivityElementRequest.builder()
                .baseElementRequest(base)
                .activityName(" ")
                .location(" ")
                .build();

        when(activityRepository.getActivityElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));

        ActivityElementDetails result = activityService.updateElement(request, baseElement, List.of());

        assertNotNull(result);
        assertEquals(element.getActivityName(), result.getActivityName());
        assertEquals(element.getLocation(), result.getLocation());
    }

    @Test
    void updateElement_whenFieldsAreValid_replaceOriginal() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.ACTIVITY);

        ActivityElement element = new ActivityElement(
                UUID.randomUUID(),
                baseElement,
                "activity",
                "location",
                LocalDateTime.now().plusDays(1),
                120,
                1
        );

        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.ACTIVITY).build();

        ActivityElementRequest request = ActivityElementRequest.builder()
                .baseElementRequest(base)
                .activityName("new activity")
                .location("new location")
                .startsAt(LocalDateTime.now().plusDays(5))
                .duration(80)
                .order(2)
                .build();

        when(activityRepository.getActivityElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));

        ActivityElementDetails result = activityService.updateElement(request, baseElement, List.of());

        assertNotNull(result);
        assertEquals(request.getActivityName(), result.getActivityName());
        assertEquals(request.getLocation(), result.getLocation());
        assertEquals(request.getStartsAt(), result.getStartsAt());
        assertEquals(request.getDuration(), result.getDuration());
        assertEquals(request.getOrder(), result.getOrder());
    }

    @Test
    void updateElement_whenDbFails_thenThrowException() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.ACTIVITY);

        ActivityElement element = new ActivityElement(
                UUID.randomUUID(),
                baseElement,
                "activity",
                "location",
                LocalDateTime.now().plusDays(1),
                120,
                1
        );

        BaseElementRequest base = BaseElementRequest.builder().elementType(ElementType.ACTIVITY).build();

        ActivityElementRequest request = ActivityElementRequest.builder()
                .baseElementRequest(base)
                .build();

        when(activityRepository.getActivityElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));
        doThrow(IllegalArgumentException.class).when(activityRepository).save(any());

        assertThrows(DbFailure.class, () -> activityService.updateElement(request, baseElement, List.of()));
    }

    @Test
    void deleteElement_whenDbFails_thenThrowException() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.ACTIVITY);

        ActivityElement element = new ActivityElement(
                UUID.randomUUID(),
                baseElement,
                "activity",
                "location",
                LocalDateTime.now().plusDays(1),
                120,
                1
        );

        when(activityRepository.getActivityElementByBaseId(baseElement.getBaseElementId())).thenReturn(Optional.of(element));
        doThrow(IllegalArgumentException.class).when(activityRepository).delete(any());

        assertThrows(DbFailure.class, () -> activityService.deleteElement(baseElement.getBaseElementId()));
    }
}