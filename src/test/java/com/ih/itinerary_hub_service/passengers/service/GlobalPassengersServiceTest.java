package com.ih.itinerary_hub_service.passengers.service;

import com.ih.itinerary_hub_service.elements.persistence.entity.BaseElement;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.passengers.persistence.entity.ElementPassenger;
import com.ih.itinerary_hub_service.passengers.persistence.entity.Passenger;
import com.ih.itinerary_hub_service.passengers.persistence.repository.ElementPassengerRepository;
import com.ih.itinerary_hub_service.passengers.persistence.repository.PassengersRepository;
import com.ih.itinerary_hub_service.passengers.requests.CreatePassengerRequest;
import com.ih.itinerary_hub_service.passengers.requests.PassengerRequest;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalPassengersServiceTest {

    @Mock
    private PassengersRepository passengersRepository;

    @Mock
    private ElementPassengerRepository elementPassengerRepository;

    @InjectMocks
    private GlobalPassengersService passengersService;

    private final UUID passengerId = UUID.randomUUID();
    private final Passenger mockPassenger = new Passenger(
            passengerId,
            "John",
            "Doe",
            "default",
            MockData.mockUser
    );
    private final BaseElement baseElement = MockData.getNewBaseElement(UUID.randomUUID(), ElementType.TRANSPORT);
    private final ElementPassenger elementPassenger = new ElementPassenger(
            UUID.randomUUID(),
            mockPassenger,
            baseElement
    );

    @Test
    void createPassenger_dbFailure() {
        CreatePassengerRequest request = new CreatePassengerRequest(
                "User",
                "Name",
                null
        );

        doThrow(IllegalArgumentException.class).when(passengersRepository).save(any());
        assertThrows(DbFailure.class, () -> passengersService.createPassenger(MockData.mockUser, request));
    }

    @Test
    void updatePassenger_dbFailure() {
        PassengerRequest request = new PassengerRequest(
                "User",
                "Name",
                " "
        );

        when(passengersRepository.findById(any())).thenReturn(Optional.of(mockPassenger));
        doThrow(IllegalArgumentException.class).when(passengersRepository).save(any());
        assertThrows(DbFailure.class, () -> passengersService.updatePassenger(UUID.randomUUID(), request));
    }

    @Test
    void deletePassenger_dbFailure() {
        when(passengersRepository.findById(any())).thenReturn(Optional.of(mockPassenger));
        doThrow(IllegalArgumentException.class).when(passengersRepository).delete(mockPassenger);
        assertThrows(DbFailure.class, () -> passengersService.deletePassenger(UUID.randomUUID()));
    }

    @Test
    void assignPassenger_dbFailure() {
        when(passengersRepository.findById(any())).thenReturn(Optional.of(mockPassenger));
        doThrow(IllegalArgumentException.class).when(elementPassengerRepository).save(any());
        assertThrows(DbFailure.class, () -> passengersService.assignPassengerToElement(passengerId, baseElement));
    }

    @Test
    void removePassenger_dbFailure() {
        when(elementPassengerRepository.findByPassengerAndElementId(passengerId, baseElement.getBaseElementId()))
                .thenReturn(Optional.of(elementPassenger));

        doThrow(IllegalArgumentException.class).when(elementPassengerRepository).delete(elementPassenger);
        assertThrows(DbFailure.class, () -> passengersService.removePassengerFromElement(passengerId, baseElement.getBaseElementId()));
    }

    @Test
    void deleteByElement_dbFailure() {
        doThrow(IllegalArgumentException.class).when(elementPassengerRepository).deleteByBaseElement(baseElement);
        assertThrows(DbFailure.class, () -> passengersService.deleteByElement(baseElement));
    }
}