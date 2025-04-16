package com.ih.itinerary_hub_service.elements.service;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.model.TransportElementDetails;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.persistence.entity.TransportElement;
import com.ih.itinerary_hub_service.elements.persistence.repository.TransportRepository;
import com.ih.itinerary_hub_service.elements.requests.TransportElementRequest;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.passengers.responses.PassengerDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TransportService {

    private final TransportRepository transportRepository;


    public TransportService(TransportRepository transportRepository) {
        this.transportRepository = transportRepository;
    }

    public TransportElementDetails createElement(TransportElementRequest request, BaseElement baseElement, List<PassengerDetails> passengerDetailsList) {
        UUID elementId = UUID.randomUUID();

        TransportElement transportElement = new TransportElement(
                elementId,
                baseElement,
                request.getOriginPlace(),
                request.getOriginDateTime(),
                request.getDestinationPlace(),
                request.getDestinationDateTime(),
                request.getOriginProvider(),
                request.getDestinationProvider(),
                request.getOrder()
        );

        try {
            transportRepository.save(transportElement);
            log.info("Created element with id {}", elementId);
            return mapElementDetails(baseElement, transportElement, passengerDetailsList);
        } catch (Exception e) {
            log.error("Failed to save transport element, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }
    }

    public void updateElementOrder(Integer order, UUID baseElementId) {
        TransportElement transportElement = getElementByBaseId(baseElementId);
        transportElement.setElementOrder(order);

        try {
            transportRepository.save(transportElement);
            log.info("Updated element order with id {}", transportElement.getElementId());
        } catch (Exception e) {
            log.error("Failed to update transport element order, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }
    }

    public TransportElementDetails updateElement(TransportElementRequest request, BaseElement baseElement, List<PassengerDetails> passengerDetailsList) {
        TransportElement transportElement = getElementByBaseId(baseElement.getBaseElementId());

        if (request.getOriginPlace() != null && !request.getOriginPlace().isBlank()) {
            transportElement.setOriginPlace(request.getOriginPlace());
        }

        if (request.getDestinationPlace() != null && !request.getDestinationPlace().isBlank()) {
            transportElement.setDestinationPlace(request.getDestinationPlace());
        }

        if (request.getOriginDateTime() != null) {
            transportElement.setOriginDateTime(request.getOriginDateTime());
        }

        if (request.getDestinationDateTime() != null) {
            transportElement.setDestinationDateTime(request.getDestinationDateTime());
        }

        if (request.getOriginProvider() != null && !request.getOriginProvider().isEmpty()) {
            if(request.getOriginProvider().isBlank()) {
                transportElement.setOriginProvider(null);
            } else {
                transportElement.setOriginProvider(request.getOriginProvider());
            }
        }

        if (request.getDestinationProvider() != null && !request.getDestinationProvider().isEmpty()) {
            if(request.getDestinationProvider().isBlank()) {
                transportElement.setDestinationProvider(null);
            } else {
                transportElement.setDestinationProvider(request.getDestinationProvider());
            }
        }

        if (request.getOrder() != null) {
            transportElement.setElementOrder(request.getOrder());
        }

        try {
            transportRepository.save(transportElement);
            log.info("Updated element with id {}", transportElement.getElementId());
            return mapElementDetails(baseElement, transportElement, passengerDetailsList);
        } catch (Exception e) {
            log.error("Failed to update transport element, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }
    }

    public void deleteElement(UUID baseElementId) {
        TransportElement transportElement = getElementByBaseId(baseElementId);

        try {
            transportRepository.delete(transportElement);
            log.info("Deleted element with id {}", transportElement.getElementId());
        } catch (Exception e) {
            log.error("Failed to delete transport element, {}", e.getMessage());
            throw new DbFailure(e.getMessage());
        }
    }

    public TransportElement getElementByBaseId(UUID baseElementID) {
        return transportRepository.getTransportElementByBaseId(baseElementID)
                .orElseThrow(() -> {
                    log.error("Couldn't find an element with base ID: {}", baseElementID);
                    return new ElementDoesNotExist("Couldn't find an element with base ID: " + baseElementID);
                });
    }

    public TransportElementDetails mapElementDetails(BaseElement baseElement, TransportElement element, List<PassengerDetails> passengerDetailsList) {
        TransportElementDetails.Builder baseElementBuild = getBaseElementBuild(baseElement, element.getElementOrder(), passengerDetailsList);

        return baseElementBuild
                .originPlace(element.getOriginPlace())
                .destinationPlace(element.getDestinationPlace())
                .originDateTime(element.getOriginDateTime())
                .destinationDateTime(element.getDestinationDateTime())
                .originProvider(element.getOriginProvider())
                .destinationProvider(element.getDestinationProvider())
                .build();
    }

    public TransportElementDetails getTransportElementDetails(BaseElement baseElement, List<PassengerDetails> passengerDetailsList) {
        TransportElement element = getElementByBaseId(baseElement.getBaseElementId());

        return mapElementDetails(baseElement, element, passengerDetailsList);
    }

    private static TransportElementDetails.Builder getBaseElementBuild(BaseElement baseElement, Integer order, List<PassengerDetails> passengerDetailsList) {
        return new TransportElementDetails.Builder()
                .baseElementID(baseElement.getBaseElementId())
                .optionID(baseElement.getOption().getOptionId())
                .lastUpdatedAt(baseElement.getLastUpdatedAt())
                .elementType(baseElement.getElementType())
                .elementCategory(baseElement.getElementCategory())
                .link(baseElement.getLink())
                .price(baseElement.getPrice())
                .notes(baseElement.getNotes())
                .status(baseElement.getStatus())
                .status(baseElement.getStatus())
                .passengerList(passengerDetailsList)
                .order(order);
    }
}
