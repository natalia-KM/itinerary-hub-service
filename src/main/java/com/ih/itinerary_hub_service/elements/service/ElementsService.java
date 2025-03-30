package com.ih.itinerary_hub_service.elements.service;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.exceptions.InvalidElementRequest;
import com.ih.itinerary_hub_service.elements.model.AccommodationElementDetails;
import com.ih.itinerary_hub_service.elements.model.ActivityElementDetails;
import com.ih.itinerary_hub_service.elements.model.BaseElementDetails;
import com.ih.itinerary_hub_service.elements.model.TransportElementDetails;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.persistence.repository.BaseElementRepository;
import com.ih.itinerary_hub_service.elements.requests.AccommodationElementRequest;
import com.ih.itinerary_hub_service.elements.requests.ActivityElementRequest;
import com.ih.itinerary_hub_service.elements.requests.BaseElementRequest;
import com.ih.itinerary_hub_service.elements.requests.TransportElementRequest;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.options.persistence.entity.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ElementsService {

    private final BaseElementRepository baseElementRepository;
    private final TransportService transportService;
    private final ActivityService activityService;
    private final AccommodationService accommodationService;

    public ElementsService(BaseElementRepository baseElementRepository, TransportService transportService, ActivityService activityService, AccommodationService accommodationService) {
        this.baseElementRepository = baseElementRepository;
        this.transportService = transportService;
        this.activityService = activityService;
        this.accommodationService = accommodationService;
    }

    public TransportElementDetails createTransportElement(Option option, TransportElementRequest request) {
        BaseElementRequest baseRequest = request.getBaseElementRequest();

        if(request.getOrder() == null || baseRequest.getElementType() != ElementType.TRANSPORT) {
            throw new InvalidElementRequest("Invalid request");
        }

        BaseElement baseElement = saveNewBaseElement(option, ElementType.TRANSPORT, baseRequest);

        return transportService.createElement(request, baseElement);
    }

    public ActivityElementDetails createActivityElement(Option option, ActivityElementRequest request) {
        BaseElementRequest baseRequest = request.getBaseElementRequest();

        if(request.getOrder() == null || baseRequest.getElementType() != ElementType.ACTIVITY) {
            throw new InvalidElementRequest("Invalid request");
        }

        BaseElement baseElement = saveNewBaseElement(option, ElementType.ACTIVITY, baseRequest);

        return activityService.createElement(request, baseElement);
    }

    public List<AccommodationElementDetails> createAccommodationsElement(Option option, AccommodationElementRequest request) {
        BaseElementRequest baseRequest = request.getBaseElementRequest();

        if(request.getCheckIn().getOrder() == null
                || request.getCheckOut().getOrder() == null
                || baseRequest.getElementType() != ElementType.ACCOMMODATION) {
            throw new InvalidElementRequest("Invalid request");
        }

        BaseElement baseElement = saveNewBaseElement(option, ElementType.ACCOMMODATION, baseRequest);

        return accommodationService.createElements(request, baseElement);
    }


    public List<BaseElementDetails> getElementsByIds(UUID optionId) {
        List<BaseElement> baseElements = baseElementRepository.findByOptionId(optionId);

        return baseElements.stream()
                .flatMap(baseElement -> {
                    switch (baseElement.getElementType()) {
                        case ACTIVITY -> {
                            return Stream.of(activityService.getElementDetailsByID(baseElement));
                        }
                        case TRANSPORT -> {
                            return Stream.of(transportService.getTransportElementDetails(baseElement));
                        }
                        case ACCOMMODATION -> {
                            return accommodationService.getAccommodationDetailsPair(baseElement).stream();
                        }
                        default -> throw new ElementDoesNotExist("Could not find base element");
                    }
                })
                .sorted(Comparator.comparing(BaseElementDetails::getOrder))
                .collect(Collectors.toList());
    }

    private BaseElement saveNewBaseElement(Option option, ElementType elementType, BaseElementRequest baseRequest) {
        BaseElement baseElement = getNewBaseElement(option, elementType, baseRequest);

        try {
            baseElementRepository.save(baseElement);
            return baseElement;
        } catch (Exception e) {
            log.error("Failed to save baseElement {}", e.getMessage());
            throw new DbFailure("Failed to save baseElement");
        }
    }

    private static BaseElement getNewBaseElement(Option option, ElementType type, BaseElementRequest baseRequest) {
        UUID baseElementId = UUID.randomUUID();
        LocalDateTime dateTime = LocalDateTime.now();

        return new BaseElement(
                baseElementId,
                option,
                dateTime,
                type,
                baseRequest.getLink(),
                baseRequest.getPrice(),
                baseRequest.getNotes(),
                baseRequest.getStatus()
        );
    }
}
