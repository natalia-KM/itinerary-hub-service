package com.ih.itinerary_hub_service.trips.service;

import com.ih.itinerary_hub_service.trips.exceptions.TripNotFound;
import com.ih.itinerary_hub_service.trips.exceptions.TripsDbFailure;
import com.ih.itinerary_hub_service.trips.persistence.entity.Trip;
import com.ih.itinerary_hub_service.trips.persistence.repository.TripsRepository;
import com.ih.itinerary_hub_service.trips.requests.CreateTripRequest;
import com.ih.itinerary_hub_service.trips.requests.UpdateTripRequest;
import com.ih.itinerary_hub_service.trips.responses.TripDetails;
import com.ih.itinerary_hub_service.users.exceptions.UserNotFoundException;
import com.ih.itinerary_hub_service.users.persistence.entity.User;
import com.ih.itinerary_hub_service.users.persistence.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TripsService {

    private final TripsRepository tripsRepository;
    private final UserRepository userRepository;

    public TripsService(TripsRepository tripsRepository, UserRepository userRepository) {
        this.tripsRepository = tripsRepository;
        this.userRepository = userRepository;
    }

    public List<TripDetails> getTrips(UUID userId) {
        List<Trip> trips = tripsRepository.findByUserId(userId);
        List<TripDetails> tripDetailsList = new ArrayList<>();

        trips.forEach(trip -> {
            TripDetails tripDetails = new TripDetails(
                    trip.getTripId(),
                    trip.getTripName(),
                    trip.getCreatedAt(),
                    trip.getImageRef(),
                    trip.getStartDate(),
                    trip.getEndDate()
            );
            tripDetailsList.add(tripDetails);
        });

        return tripDetailsList;
    }

    // TODO: modify once everything is setup in DB
    public TripDetails getTripById(UUID userId, UUID tripId) {
        Trip trip = tripsRepository.findByTripIdAndUserId(tripId, userId)
                .orElseThrow(() -> {
                    log.error("Trip not found with ID: {} and userId: {}", tripId, userId);
                     return new TripNotFound("Trip not found");
                });

        return new TripDetails(
                trip.getTripId(),
                trip.getTripName(),
                trip.getCreatedAt(),
                trip.getImageRef(),
                trip.getStartDate(),
                trip.getEndDate()
        );
    }

    public void createTrip(UUID userId, CreateTripRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        UUID tripId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime startDate = request.startDate().orElse(null);
        LocalDateTime endDate = request.endDate().orElse(null);
        String imageRef = request.imageRef().orElse("default");

        Trip newTrip = new Trip(
                tripId,
                user,
                request.tripName().trim(),
                createdAt,
                startDate,
                endDate,
                imageRef
        );

        try {
            tripsRepository.save(newTrip);
            log.info("Trip created: {}", tripId);
        } catch (Exception e) {
            log.error("Failed to create a trip: {}", e.getMessage());
            throw new TripsDbFailure("Failed to update trip details");
        }
    }

    public void updateTrip(UUID userId, UUID tripId, UpdateTripRequest request) {
        Trip existingTrip = tripsRepository.findByTripIdAndUserId(tripId, userId)
                .orElseThrow(() -> {
                    log.error("Trip not found with ID: {} and userId: {}", tripId, userId);
                    return new TripNotFound("Trip not found");
                });

        request.tripName()
                .filter(name -> !name.isBlank())
                .ifPresent(existingTrip::setTripName);

        request.imageRef()
                .filter(ref -> !ref.isBlank())
                .ifPresent(existingTrip::setImageRef);

        request.startDate().ifPresent(existingTrip::setStartDate);
        request.endDate().ifPresent(existingTrip::setEndDate);

        try {
            tripsRepository.save(existingTrip);
            log.info("Trip details updated for trip ID: {}", existingTrip.getTripId());
        } catch (Exception e) {
            log.error("Failed to update trip details: {}", e.getMessage());
            throw new TripsDbFailure("Failed to update trip details");
        }
    }

    public void deleteTrip(UUID userId, UUID tripId) {
        // to make sure the user owns the trip
        Trip existingTrip = tripsRepository.findByTripIdAndUserId(tripId, userId)
                .orElseThrow(() -> {
                    log.error("Trip not found with ID: {} and userId: {}", tripId, userId);
                    return new TripNotFound("Trip not found");
                });

        try {
            tripsRepository.deleteById(existingTrip.getTripId());
            log.info("Trip deleted, ID: {}", existingTrip.getTripId());
        } catch (Exception e) {
            log.error("Failed to delete the trip: {}", e.getMessage());
            throw new TripsDbFailure("Failed to delete the trip");
        }
    }
}
