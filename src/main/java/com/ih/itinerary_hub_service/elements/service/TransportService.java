package com.ih.itinerary_hub_service.elements.service;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.model.TransportElementDetails;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.persistence.entity.TransportElement;
import com.ih.itinerary_hub_service.elements.persistence.repository.TransportRepository;
import com.ih.itinerary_hub_service.elements.requests.TransportElementRequest;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class TransportService {

    private final TransportRepository transportRepository;


    public TransportService(TransportRepository transportRepository) {
        this.transportRepository = transportRepository;
    }

    public TransportElementDetails createElement(TransportElementRequest request, BaseElement baseElement) {
        UUID elementId = UUID.randomUUID();

        TransportElement transportElement = new TransportElement(
                elementId,
                baseElement,
                request.getOriginPlace(),
                request.getOriginDateTime(),
                request.getDestinationPlace(),
                request.getDestinationDateTime(),
                request.getProvider(),
                request.getOrder()
        );

        try {
            transportRepository.save(transportElement);
            log.info("Created element with id {}", elementId);
            return mapElementDetails(baseElement, transportElement);
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

    public TransportElementDetails updateElement(TransportElementRequest request, BaseElement baseElement) {
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

        if (request.getProvider() != null && !request.getProvider().isEmpty()) {
            if(request.getProvider().isBlank()) {
                transportElement.setProvider(null);
            } else {
                transportElement.setProvider(request.getProvider());
            }
        }

        if (request.getOrder() != null) {
            transportElement.setElementOrder(request.getOrder());
        }

        try {
            transportRepository.save(transportElement);
            log.info("Updated element with id {}", transportElement.getElementId());
            return mapElementDetails(baseElement, transportElement);
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

    public TransportElementDetails mapElementDetails(BaseElement baseElement, TransportElement element) {
        TransportElementDetails.Builder baseElementBuild = getBaseElementBuild(baseElement, element.getElementOrder());

        return baseElementBuild
                .originPlace(element.getOriginPlace())
                .destinationPlace(element.getDestinationPlace())
                .originDateTime(element.getOriginDateTime())
                .destinationDateTime(element.getDestinationDateTime())
                .provider(element.getProvider())
                .build();
    }

    public TransportElementDetails getTransportElementDetails(BaseElement baseElement) {
        TransportElement element = getElementByBaseId(baseElement.getBaseElementId());

        return mapElementDetails(baseElement, element);
    }

    private static TransportElementDetails.Builder getBaseElementBuild(BaseElement baseElement, Integer order) {
        return new TransportElementDetails.Builder()
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
