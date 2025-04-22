package com.ih.itinerary_hub_service.passengers.service;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.passengers.persistence.entity.ElementPassenger;
import com.ih.itinerary_hub_service.passengers.persistence.entity.Passenger;
import com.ih.itinerary_hub_service.passengers.persistence.repository.ElementPassengerRepository;
import com.ih.itinerary_hub_service.passengers.persistence.repository.PassengersRepository;
import com.ih.itinerary_hub_service.passengers.requests.CreatePassengerRequest;
import com.ih.itinerary_hub_service.passengers.requests.PassengerRequest;
import com.ih.itinerary_hub_service.passengers.responses.PassengerDetails;
import com.ih.itinerary_hub_service.users.persistence.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GlobalPassengersService {

    private final PassengersRepository passengersRepository;
    private final ElementPassengerRepository elementPassengerRepository;

    public GlobalPassengersService(PassengersRepository passengersRepository, ElementPassengerRepository elementPassengerRepository) {
        this.passengersRepository = passengersRepository;
        this.elementPassengerRepository = elementPassengerRepository;
    }

    public List<PassengerDetails> getAllPassengersInAccount(UUID userId) {
        return passengersRepository.findByUserId(userId)
                .stream()
                .map(this::maptoPassengerDetails)
                .collect(Collectors.toList());
    }

    public PassengerDetails getPassengerDetails(UUID passengerId) {
        Passenger existingPassenger = getPassenger(passengerId);
        return maptoPassengerDetails(existingPassenger);
    }

    public PassengerDetails createPassenger(User user, CreatePassengerRequest request) {
        UUID passengerId = UUID.randomUUID();

        Passenger passenger = new Passenger(
                passengerId,
                request.firstName().trim(),
                request.lastName().trim(),
                request.avatar(),
                user
        );

        try {
            passengersRepository.save(passenger);
            log.info("Passenger created: {}", passengerId);
            return maptoPassengerDetails(passenger);
        } catch (Exception e) {
            log.error("Error creating passenger", e);
            throw new DbFailure("Failed to create passenger");
        }
    }

    public void updatePassenger(UUID passengerId, PassengerRequest request) {
        Passenger passenger = getPassenger(passengerId);

        if(request.firstName() != null && !request.firstName().isBlank()) {
            passenger.setFirstName(request.firstName().trim());
        }

        if(request.lastName() != null && !request.lastName().isBlank()) {
            passenger.setLastName(request.lastName().trim());
        }

        if(request.avatar() != null && !request.avatar().isBlank()) {
            passenger.setAvatar(request.avatar());
        }

        try {
            passengersRepository.save(passenger);
            log.info("Passenger updated: {}", passengerId);
        } catch (Exception e) {
            log.error("Error updating passenger", e);
            throw new DbFailure("Failed to update passenger");
        }
    }

    public void deletePassenger(UUID passengerId) {
        Passenger passenger = getPassenger(passengerId);

        try {
            passengersRepository.delete(passenger);
            log.info("Passenger deleted: {}", passengerId);
        } catch (Exception e) {
            log.error("Error deleting passenger", e);
            throw new DbFailure("Failed to delete passenger");
        }
    }

    public List<PassengerDetails> getAllPassengersInElement(BaseElement baseElement) {
        List<ElementPassenger> elementPassengerList = elementPassengerRepository.findByBaseElement(baseElement);

        return elementPassengerList
                .stream()
                .map(elementPassenger -> getPassengerDetails(elementPassenger.getPassenger().getPassengerId()))
                .collect(Collectors.toList());
    }

    public void assignPassengerToElement(UUID passengerId, BaseElement baseElement) {
        Passenger passenger = getPassenger(passengerId);

        ElementPassenger elementPassenger = new ElementPassenger(
                UUID.randomUUID(),
                passenger,
                baseElement
        );

        try {
            elementPassengerRepository.save(elementPassenger);
            log.info("Passenger assigned: {}", passengerId);
        } catch (Exception e) {
            log.error("Error assigning passenger", e);
            throw new DbFailure("Failed to assign passenger");
        }
    }

    public void removePassengerFromElement(UUID passengerId, UUID baseElementId) {
        ElementPassenger el = elementPassengerRepository.findByPassengerAndElementId(passengerId, baseElementId)
                .orElseThrow(() -> new ElementDoesNotExist("Passenger not found"));

        try {
            elementPassengerRepository.delete(el);
            log.info("Element passenger removed: {}", el);
        } catch (Exception e) {
            log.error("Error deleting passenger", e);
            throw new DbFailure("Failed to delete passenger");
        }
    }

    public List<PassengerDetails> updateAllPassengersInElement(List<UUID> passengersToUpdate, BaseElement baseElement) {
        List<UUID> existingPassengerList = elementPassengerRepository.findPassengersIdsByElementId(baseElement.getBaseElementId());

        if(passengersToUpdate == null) {
            return getAllPassengersInElement(baseElement);
        }

        Set<UUID> existingSet = new HashSet<>(existingPassengerList);
        Set<UUID> requestedSet = new HashSet<>(passengersToUpdate);

        Set<UUID> toAdd = new HashSet<>(requestedSet);
        toAdd.removeAll(existingSet);

        Set<UUID> toRemove = new HashSet<>(existingSet);
        toRemove.removeAll(requestedSet);

        toAdd.forEach(passengerId -> assignPassengerToElement(passengerId, baseElement));

        toRemove.forEach(passengerId -> removePassengerFromElement(passengerId, baseElement.getBaseElementId()));

        return getAllPassengersInElement(baseElement);
    }

    public void deleteByElement(BaseElement baseElement) {
        try {
            elementPassengerRepository.deleteByBaseElement(baseElement);
            log.info("Successfully deleted element: {}", baseElement);
        } catch (Exception e) {
            log.error("Error deleting element", e);
            throw new DbFailure("Failed to delete element");
        }
    }

    private Passenger getPassenger(UUID passengerId) {
        return passengersRepository.findById(passengerId)
                .orElseThrow(() -> {
                    log.error("Passenger with id {} not found", passengerId);
                    return new DbFailure("Passenger not found");
                });
    }

    protected PassengerDetails maptoPassengerDetails(Passenger passenger) {
        return new PassengerDetails(
                passenger.getPassengerId(),
                passenger.getFirstName(),
                passenger.getLastName(),
                passenger.getAvatar()
        );
    }
}
