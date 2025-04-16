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
import com.ih.itinerary_hub_service.elements.types.AccommodationType;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.passengers.responses.PassengerDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AccommodationService {

    private final AccommodationElementRepository accommodationElementRepository;
    private final AccommodationEventRepository accommodationEventRepository;

    public AccommodationService(AccommodationElementRepository accommodationElementRepository, AccommodationEventRepository accommodationEventRepository) {
        this.accommodationElementRepository = accommodationElementRepository;
        this.accommodationEventRepository = accommodationEventRepository;
    }

    public List<AccommodationElementDetails> createElements(AccommodationElementRequest request, BaseElement baseElement, List<PassengerDetails> passengerDetailsList) {
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

        return mapAccommodationElementDetails(accommodationElement, List.of(checkIn, checkOut), baseElement, passengerDetailsList);
    }

    public void updateElementOrder(Integer order, UUID baseElementID, Optional<AccommodationType> type) {
        if(type.isEmpty()) {
            throw new InvalidElementRequest("Accommodation type must be specified to update acc elements order");
        }

        AccommodationElement accommodationElement = getElementByBaseId(baseElementID);
        AccommodationEvent existingEvent = getSingleAccEvent(accommodationElement, type.get());

        existingEvent.setElementOrder(order);

        try {
            accommodationEventRepository.save(existingEvent);

            log.info("Updated accommodation event order with id: {}", existingEvent.getEventId());
        } catch (Exception e) {
            log.error("Failed to update accommodation event order, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }
    }

    public List<AccommodationElementDetails> updateAccommodationElements(AccommodationElementRequest request, BaseElement baseElement, List<PassengerDetails> passengerDetailsList) {
        AccommodationElement accommodationElement = getElementByBaseId(baseElement.getBaseElementId());

        if (request.getPlace() != null && !request.getPlace().isBlank()) {
            accommodationElement.setPlace(request.getPlace());
        }

        if (request.getLocation() != null && !request.getLocation().isEmpty()) {
            if(request.getLocation().isBlank()) {
                accommodationElement.setLocation(null);
            } else {
                accommodationElement.setLocation(request.getLocation());
            }
        }

        try {
            accommodationElementRepository.save(accommodationElement);
            log.info("Updated acc element with id {}", accommodationElement.getElementId());
        } catch (Exception e) {
            log.error("Failed to update accommodation element, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }

        AccommodationEvent existingCheckIn = getSingleAccEvent(accommodationElement, AccommodationType.CHECK_IN);
        AccommodationEvent existingCheckOut = getSingleAccEvent(accommodationElement, AccommodationType.CHECK_OUT);

        if(request.getCheckIn() != null) {
            AccommodationEventRequest checkInRequest = request.getCheckIn();
            if(checkInRequest.getDateTime() != null) {
                existingCheckIn.setDatetime(checkInRequest.getDateTime());
            }
            if(checkInRequest.getOrder() != null) {
                existingCheckIn.setElementOrder(checkInRequest.getOrder());
            }
        }

        if(request.getCheckOut() != null) {
            AccommodationEventRequest checkOutRequest = request.getCheckOut();
            if(checkOutRequest.getDateTime() != null) {
                existingCheckOut.setDatetime(checkOutRequest.getDateTime());
            }
            if(checkOutRequest.getOrder() != null) {
                existingCheckOut.setElementOrder(checkOutRequest.getOrder());
            }
        }
        try {
            accommodationEventRepository.save(existingCheckIn);
            accommodationEventRepository.save(existingCheckOut);

            log.info("Updated accommodation events with ids: {}, {}", existingCheckIn.getEventId(), existingCheckOut.getEventId());
        } catch (Exception e) {
            log.error("Failed to update accommodation events, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }

        List<AccommodationEvent> updatedEvent = Arrays.asList(existingCheckIn, existingCheckOut);
        return mapAccommodationElementDetails(accommodationElement, updatedEvent, baseElement, passengerDetailsList);
    }

    public void deleteElement(UUID baseElementID) {
        AccommodationElement accommodationElement = getElementByBaseId(baseElementID);
        List<AccommodationEvent> accommodationEvents = getAccommodationEventsPair(baseElementID, accommodationElement.getElementId());

        try {
            accommodationEventRepository.deleteAll(accommodationEvents);
            accommodationElementRepository.delete(accommodationElement);
            log.info("Deleted accommodation element with id {}", accommodationElement.getElementId());
        } catch (Exception e) {
            log.error("Failed to delete accommodation element, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }
    }


    public List<AccommodationElementDetails> getAccommodationDetailsPair(BaseElement baseElement, List<PassengerDetails> passengerDetailsList) {
        AccommodationElement accommodationElement = getElementByBaseId(baseElement.getBaseElementId());
        List<AccommodationEvent> accommodationEvents = getAccommodationEventsPair(baseElement.getBaseElementId(), accommodationElement.getElementId());

        return mapAccommodationElementDetails(accommodationElement, accommodationEvents, baseElement, passengerDetailsList);
    }

    public AccommodationElementDetails getSingleAccommodationDetailsElement(BaseElement baseElement, AccommodationType accommodationType, List<PassengerDetails> passengerDetailsList) {
        AccommodationElement accommodationElement = getElementByBaseId(baseElement.getBaseElementId());
        AccommodationEvent event = getSingleAccEvent(accommodationElement, accommodationType);

        return mapSingleAccElementDetails(accommodationElement, event, baseElement, passengerDetailsList);
    }

    private AccommodationEvent getSingleAccEvent(AccommodationElement accommodationElement, AccommodationType accommodationType) {
        Optional<AccommodationEvent> event = accommodationEventRepository.getAccommodationEventsByAccommodationIdAndType(accommodationElement.getElementId(), accommodationType);

        if(event.isEmpty() || event.get().getType() != accommodationType) {
            throw new ElementDoesNotExist("Couldn't find the matching accommodation events");
        }
        return event.get();
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


    private static List<AccommodationElementDetails> mapAccommodationElementDetails(AccommodationElement element, List<AccommodationEvent> events, BaseElement baseElement, List<PassengerDetails> passengerDetailsList) {
        List<AccommodationElementDetails> accommodationElementDetails = new ArrayList<>();

        for(AccommodationEvent accommodationEvent : events) {
            AccommodationElementDetails elementDetails = mapSingleAccElementDetails(element, accommodationEvent, baseElement, passengerDetailsList);
            accommodationElementDetails.add(elementDetails);
        }

        return accommodationElementDetails;
    }

    private static AccommodationElementDetails mapSingleAccElementDetails(AccommodationElement element, AccommodationEvent accommodationEvent, BaseElement baseElement, List<PassengerDetails> passengerDetailsList) {
        AccommodationElementDetails.Builder baseElementBuild = getBaseElementBuild(baseElement, accommodationEvent.getElementOrder(), passengerDetailsList);

        return baseElementBuild
                        .place(element.getPlace())
                        .location(element.getLocation())
                        .accommodationType(accommodationEvent.getType())
                        .dateTime(accommodationEvent.getDatetime())
                        .build();
    }

    private static AccommodationElementDetails.Builder getBaseElementBuild(BaseElement baseElement, Integer order, List<PassengerDetails> passengerDetailsList) {
        return new AccommodationElementDetails.Builder()
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
