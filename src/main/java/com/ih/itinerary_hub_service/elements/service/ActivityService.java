package com.ih.itinerary_hub_service.elements.service;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.model.ActivityElementDetails;
import com.ih.itinerary_hub_service.elements.persistence.entity.ActivityElement;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.persistence.repository.ActivityRepository;
import com.ih.itinerary_hub_service.elements.requests.ActivityElementRequest;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.passengers.responses.PassengerDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ActivityElementDetails createElement(ActivityElementRequest request, BaseElement baseElement, List<PassengerDetails> passengerDetailsList) {
        UUID elementId = UUID.randomUUID();

        ActivityElement activityElement = new ActivityElement(
                elementId,
                baseElement,
                request.getActivityName(),
                request.getLocation(),
                request.getStartsAt(),
                request.getDuration(),
                request.getOrder()
        );

        try {
            activityRepository.save(activityElement);
            log.info("Created element with id {}", elementId);
            return mapElementDetails(baseElement, activityElement, passengerDetailsList);
        } catch (Exception e) {
            log.error("Failed to save activity element, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }
    }

    public void updateElementOrder(Integer order, UUID baseElementID) {
        ActivityElement activityElement = getElementById(baseElementID);
        activityElement.setElementOrder(order);

        try {
            activityRepository.save(activityElement);
            log.info("Updated element order with id {}", activityElement.getElementId());
        } catch (Exception e) {
            log.error("Failed to update activity element order, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }
    }

    public ActivityElementDetails updateElement(ActivityElementRequest request, BaseElement baseElement, List<PassengerDetails> passengerDetailsList) {
        ActivityElement activityElement = getElementById(baseElement.getBaseElementId());

        if (request.getActivityName() != null && !request.getActivityName().isBlank()) {
            activityElement.setActivityName(request.getActivityName());
        }

        if (request.getLocation() != null && !request.getLocation().isBlank()) {
            activityElement.setLocation(request.getLocation());
        }

        if (request.getStartsAt() != null) {
            activityElement.setStartsAt(request.getStartsAt());
        }

        if (request.getDuration() != null) {
            activityElement.setDuration(request.getDuration());
        }

        if (request.getOrder() != null) {
            activityElement.setElementOrder(request.getOrder());
        }

        try {
            activityRepository.save(activityElement);
            log.info("Updated element with id {}", activityElement.getElementId());
            return mapElementDetails(baseElement, activityElement, passengerDetailsList);
        } catch (Exception e) {
            log.error("Failed to update activity element, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }
    }

    public void deleteElement(UUID baseElementID) {
        ActivityElement activityElement = getElementById(baseElementID);

        try {
            activityRepository.delete(activityElement);
            log.info("Deleted element with id {}", activityElement.getElementId());
        } catch (Exception e) {
            log.error("Failed to delete activity element, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }
    }

    public ActivityElement getElementById(UUID baseElementID) {
        return activityRepository.getActivityElementByBaseId(baseElementID)
                .orElseThrow(() -> {
                    log.error("Couldn't find an element with base ID: {}", baseElementID);
                    return new ElementDoesNotExist("Couldn't find an element with base ID: " + baseElementID);
                });
    }

    public ActivityElementDetails mapElementDetails(BaseElement baseElement, ActivityElement element, List<PassengerDetails> passengerDetailsList) {
        ActivityElementDetails.Builder baseElementBuild = getBaseElementBuild(baseElement, element.getElementOrder(), passengerDetailsList);

        return baseElementBuild
                .elementID(element.getElementId())
                .activityName(element.getActivityName())
                .location(element.getLocation())
                .startsAt(element.getStartsAt())
                .duration(element.getDuration())
                .build();
    }


    public ActivityElementDetails getElementDetailsByID(BaseElement baseElement, List<PassengerDetails> passengerDetailsList) {
        ActivityElement element = getElementById(baseElement.getBaseElementId());

        return mapElementDetails(baseElement, element, passengerDetailsList);
    }

    private static ActivityElementDetails.Builder getBaseElementBuild(BaseElement baseElement, Integer order, List<PassengerDetails> passengerDetailsList) {
        return new ActivityElementDetails.Builder()
                .baseElementID(baseElement.getBaseElementId())
                .optionID(baseElement.getOption().getOptionId())
                .lastUpdatedAt(baseElement.getLastUpdatedAt())
                .elementType(baseElement.getElementType())
                .elementCategory(baseElement.getElementCategory())
                .link(baseElement.getLink())
                .price(baseElement.getPrice())
                .notes(baseElement.getNotes())
                .status(baseElement.getStatus())
                .passengerList(passengerDetailsList)
                .order(order);
    }
}
