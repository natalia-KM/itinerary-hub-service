package com.ih.itinerary_hub_service.unit.trips;

import com.ih.itinerary_hub_service.trips.exceptions.TripNotFound;
import com.ih.itinerary_hub_service.trips.exceptions.TripsDbFailure;
import com.ih.itinerary_hub_service.trips.persistence.entity.Trip;
import com.ih.itinerary_hub_service.trips.persistence.repository.TripsRepository;
import com.ih.itinerary_hub_service.trips.requests.CreateTripRequest;
import com.ih.itinerary_hub_service.trips.requests.UpdateTripRequest;
import com.ih.itinerary_hub_service.trips.service.TripsService;
import com.ih.itinerary_hub_service.users.exceptions.UserNotFoundException;
import com.ih.itinerary_hub_service.users.persistence.entity.User;
import com.ih.itinerary_hub_service.users.persistence.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripsServiceTest {

    @Mock
    private TripsRepository tripsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TripsService tripsService;

    private static final UUID userId = UUID.randomUUID();
    private static final UUID tripId = UUID.randomUUID();
    private static final LocalDateTime DATE = LocalDateTime.of(2024, 5, 12, 0, 0, 0);
    private static final String newTripName = "Dubai Trip";
    private static final String newTripImageRef = "image-1";
    private static final User mockUser = new User(
            userId,
            "John",
            "Doe",
            LocalDateTime.now(),
            true,
            null,
            null,
            null
    );
    private static final Trip newTrip = new Trip(tripId, mockUser, newTripName, DATE, DATE, DATE, newTripImageRef);

    @Nested
    class CreateTrip {

        CreateTripRequest request = new CreateTripRequest(
                newTripName,
                Optional.of(DATE),
                Optional.empty(),
                Optional.of(newTripImageRef)
        );

        @Test
        void createTrip_shouldThrow_whenUserDoesNotExist() {
            when(userRepository.findById(any())).thenReturn(Optional.empty());
            assertThrows(UserNotFoundException.class, () -> tripsService.createTrip(tripId, request));
        }

        @Test
        void createTrip_shouldThrow_whenUserDbFailed() {
            when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));
            when(tripsRepository.save(any())).thenThrow(new IllegalStateException());
            assertThrows(TripsDbFailure.class, () -> tripsService.createTrip(tripId, request));
        }
    }

    @Nested
    class UpdateTrip {
        UpdateTripRequest request = new UpdateTripRequest(
                Optional.of(newTripName),
                Optional.of(DATE),
                Optional.empty(),
                Optional.empty()
        );

        @Test
        void updateTrip_shouldThrow_whenUserDoesNotExist() {
            when(tripsRepository.findByTripIdAndUserId(any(), any())).thenReturn(Optional.empty());
            assertThrows(TripNotFound.class, () -> tripsService.updateTrip(userId, tripId, request));
        }

        @Test
        void updateTrip_shouldThrow_whenUserDbFailed() {
            when(tripsRepository.findByTripIdAndUserId(any(), any())).thenReturn(Optional.of(newTrip));
            when(tripsRepository.save(any())).thenThrow(new IllegalStateException());
            assertThrows(TripsDbFailure.class, () -> tripsService.updateTrip(userId, tripId, request));
        }
    }

    @Nested
    class DeleteTrip {
        @Test
        void deleteTrip_shouldThrow_whenUserDoesNotExist() {
            when(tripsRepository.findByTripIdAndUserId(any(), any())).thenReturn(Optional.empty());
            assertThrows(TripNotFound.class, () -> tripsService.deleteTrip(userId, tripId));
        }

        @Test
        void deleteTrip_shouldThrow_whenUserDbFailed() {
            when(tripsRepository.findByTripIdAndUserId(any(), any())).thenReturn(Optional.of(newTrip));
            doThrow(new IllegalArgumentException()).when(tripsRepository).deleteById(any());
            assertThrows(TripsDbFailure.class, () -> tripsService.deleteTrip(userId, tripId));
        }
    }
}