package com.ih.itinerary_hub_service.unit.trips;

import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.trips.exceptions.TripNotFound;
import com.ih.itinerary_hub_service.trips.persistence.repository.TripsRepository;
import com.ih.itinerary_hub_service.trips.requests.CreateTripRequest;
import com.ih.itinerary_hub_service.trips.requests.UpdateTripRequest;
import com.ih.itinerary_hub_service.trips.service.TripsService;
import com.ih.itinerary_hub_service.users.exceptions.UserNotFoundException;
import com.ih.itinerary_hub_service.users.persistence.repository.UserRepository;
import com.ih.itinerary_hub_service.utils.MockData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Nested
    class CreateTrip {

        CreateTripRequest request = new CreateTripRequest(
                MockData.tripName,
                Optional.of(MockData.DATE),
                Optional.empty(),
                Optional.of(MockData.imageRef)
        );

        @Test
        void createTrip_shouldThrow_whenUserDoesNotExist() {
            when(userRepository.findById(any())).thenReturn(Optional.empty());
            assertThrows(UserNotFoundException.class, () -> tripsService.createTrip(MockData.tripId, request));
        }

        @Test
        void createTrip_shouldThrow_whenUserDbFailed() {
            when(userRepository.findById(any())).thenReturn(Optional.of(MockData.mockUser));
            when(tripsRepository.save(any())).thenThrow(new IllegalStateException());
            assertThrows(DbFailure.class, () -> tripsService.createTrip(MockData.tripId, request));
        }
    }

    @Nested
    class UpdateTrip {
        UpdateTripRequest request = new UpdateTripRequest(
                Optional.of(MockData.tripName),
                Optional.of(MockData.DATE),
                Optional.empty(),
                Optional.empty()
        );

        @Test
        void updateTrip_shouldThrow_whenUserDoesNotExist() {
            when(tripsRepository.findByTripIdAndUserId(any(), any())).thenReturn(Optional.empty());
            assertThrows(TripNotFound.class, () -> tripsService.updateTrip(MockData.userId, MockData.tripId, request));
        }

        @Test
        void updateTrip_shouldThrow_whenUserDbFailed() {
            when(tripsRepository.findByTripIdAndUserId(any(), any())).thenReturn(Optional.of(MockData.mockTrip));
            when(tripsRepository.save(any())).thenThrow(new IllegalStateException());
            assertThrows(DbFailure.class, () -> tripsService.updateTrip(MockData.userId, MockData.tripId, request));
        }
    }

    @Nested
    class DeleteTrip {
        @Test
        void deleteTrip_shouldThrow_whenUserDoesNotExist() {
            when(tripsRepository.findByTripIdAndUserId(any(), any())).thenReturn(Optional.empty());
            assertThrows(TripNotFound.class, () -> tripsService.deleteTrip(MockData.userId, MockData.tripId));
        }

        @Test
        void deleteTrip_shouldThrow_whenUserDbFailed() {
            when(tripsRepository.findByTripIdAndUserId(any(), any())).thenReturn(Optional.of(MockData.mockTrip));
            doThrow(new IllegalArgumentException()).when(tripsRepository).deleteTripById(any());
            assertThrows(DbFailure.class, () -> tripsService.deleteTrip(MockData.userId, MockData.tripId));
        }
    }
}