package com.ih.itinerary_hub_service.elements.service;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.model.AccommodationElementDetails;
import com.ih.itinerary_hub_service.elements.persistence.entity.AccommodationElement;
import com.ih.itinerary_hub_service.elements.persistence.entity.AccommodationEvent;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.persistence.repository.AccommodationElementRepository;
import com.ih.itinerary_hub_service.elements.persistence.repository.AccommodationEventRepository;
import com.ih.itinerary_hub_service.elements.requests.AccommodationElementRequest;
import com.ih.itinerary_hub_service.elements.types.AccommodationType;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AccommodationService {

    private final AccommodationElementRepository accommodationElementRepository;
    private final AccommodationEventRepository accommodationEventRepository;

    public AccommodationService(AccommodationElementRepository accommodationElementRepository, AccommodationEventRepository accommodationEventRepository) {
        this.accommodationElementRepository = accommodationElementRepository;
        this.accommodationEventRepository = accommodationEventRepository;
    }

    public List<AccommodationElementDetails> createElements(AccommodationElementRequest request, BaseElement baseElement) {
        UUID elementId = UUID.randomUUID();

        AccommodationElement accommodationElement = new AccommodationElement(
                elementId,
                baseElement,
                request.getPlace(),
                request.getLocation()
        );

        try {
            accommodationElementRepository.save(accommodationElement);
            log.info("Created element with id {}", elementId);
        } catch (Exception e) {
            log.error("Failed to save accommodation element, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }

        UUID checkInId = UUID.randomUUID();
        UUID checkOutId = UUID.randomUUID();

        AccommodationEvent checkIn = new AccommodationEvent(
                checkInId,
                accommodationElement,
                AccommodationType.CHECK_IN,
                request.getCheckIn().getDateTime(),
                request.getCheckIn().getOrder()
        );
        AccommodationEvent checkOut = new AccommodationEvent(
                checkOutId,
                accommodationElement,
                AccommodationType.CHECK_OUT,
                request.getCheckOut().getDateTime(),
                request.getCheckOut().getOrder()
        );

        try {
            accommodationEventRepository.save(checkIn);
            accommodationEventRepository.save(checkOut);

            log.info("Created accommodation events with ids: {}, {}", checkInId, checkOutId);
        } catch (Exception e) {
            log.error("Failed to save accommodation events, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }

        return mapAccommodationElementDetails(accommodationElement, List.of(checkIn, checkOut), baseElement);
    }

    public List<AccommodationElementDetails> getAccommodationDetailsPair(BaseElement baseElement) {
        AccommodationElement accommodationElement = getElementByBaseId(baseElement.getBaseElementId());
        List<AccommodationEvent> accommodationEvents = getAccommodationEventsPair(baseElement.getBaseElementId(), accommodationElement.getElementId());

        return mapAccommodationElementDetails(accommodationElement, accommodationEvents, baseElement);
    }

    public AccommodationElementDetails getSingleAccommodationDetailsElement(BaseElement baseElement, AccommodationType accommodationType) {
        List<AccommodationElementDetails> pair = getAccommodationDetailsPair(baseElement);

        Optional<AccommodationElementDetails> elementDetails = pair.stream()
                .filter(el -> el.getAccommodationType().equals(accommodationType))
                .findFirst();

        if(elementDetails.isEmpty()) {
            throw new ElementDoesNotExist("Couldn't find the matching accommodation elements");
        }
        return elementDetails.get();
    }

    private AccommodationElement getElementByBaseId(UUID baseElementID) {
        return accommodationElementRepository.getAccommElementByBaseId(baseElementID)
                .orElseThrow(() -> {
                    log.error("Couldn't find an element with base ID: {}", baseElementID);
                    return new ElementDoesNotExist("Couldn't find an element with base ID: " + baseElementID);
                });
    }

    private List<AccommodationEvent> getAccommodationEventsPair(UUID baseElementID, UUID accommodationElementID) {
        List<AccommodationEvent> accommodationEvents = accommodationEventRepository.getAccommodationEventsByAccommodationId(accommodationElementID);

        if(accommodationEvents.size() != 2) {
            log.error("Couldn't find the matching accommodation elements with base ID: {} and accommID: {}", baseElementID, accommodationElementID);
            throw new ElementDoesNotExist("Couldn't find the matching accommodation elements");
        }
        return accommodationEvents;
    }


    private static List<AccommodationElementDetails> mapAccommodationElementDetails(AccommodationElement element, List<AccommodationEvent> events, BaseElement baseElement) {
        List<AccommodationElementDetails> accommodationElementDetails = new ArrayList<>();

        for(AccommodationEvent accommodationEvent : events) {
            AccommodationElementDetails.Builder baseElementBuild = getBaseElementBuild(baseElement, accommodationEvent.getElementOrder());

            AccommodationElementDetails elementDetails =
                    baseElementBuild
                            .place(element.getPlace())
                            .location(element.getLocation())
                            .accommodationType(accommodationEvent.getType())
                            .dateTime(accommodationEvent.getDatetime())
                            .build();

            accommodationElementDetails.add(elementDetails);
        }

        return accommodationElementDetails;
    }

    private static AccommodationElementDetails.Builder getBaseElementBuild(BaseElement baseElement, Integer order) {
        return new AccommodationElementDetails.Builder()
                .baseElementID(baseElement.getBaseElementId())
                .optionID(baseElement.getOption().getOptionId())
                .lastUpdatedAt(baseElement.getLastUpdatedAt())
                .elementType(baseElement.getElementType())
                .link(baseElement.getLink())
                .price(baseElement.getPrice())
                .notes(baseElement.getNotes())
                .status(baseElement.getStatus())
                .order(order);
    }
}
