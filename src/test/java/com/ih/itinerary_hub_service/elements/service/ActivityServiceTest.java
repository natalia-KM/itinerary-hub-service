package com.ih.itinerary_hub_service.elements.service;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.persistence.repository.ActivityRepository;
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
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    @Test
    void createActivity_whenDbFail_shouldThrowException() {
        BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.ACTIVITY);
        when(activityRepository.save(any())).thenThrow(new IllegalArgumentException());

        assertThrows(DbFailure.class, () -> activityService.createElement(MockData.mockActivityRequest, baseElement));
    }

    @Test
    void getElement_whenElementNotFound_shouldThrowException() {
        UUID baseElementID = UUID.randomUUID();
        when(activityRepository.getActivityElementByBaseId(baseElementID)).thenReturn(Optional.empty());

        assertThrows(ElementDoesNotExist.class, () -> activityService.getElementById(baseElementID));
    }
}