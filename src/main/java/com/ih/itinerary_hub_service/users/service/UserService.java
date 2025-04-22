package com.ih.itinerary_hub_service.users.service;

import com.ih.itinerary_hub_service.passengers.requests.CreatePassengerRequest;
import com.ih.itinerary_hub_service.passengers.service.GlobalPassengersService;
import com.ih.itinerary_hub_service.users.exceptions.UserAlreadyExists;
import com.ih.itinerary_hub_service.users.exceptions.UserNotFoundException;
import com.ih.itinerary_hub_service.users.persistence.entity.User;
import com.ih.itinerary_hub_service.users.persistence.repository.UserRepository;
import com.ih.itinerary_hub_service.users.requests.UpdateUserDetailsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final GlobalPassengersService passengersService;

    public UserService(UserRepository userRepository, GlobalPassengersService passengersService) {
        this.userRepository = userRepository;
        this.passengersService = passengersService;
    }

    public User createGuestUser(String firstName, String lastName) {
        UUID userId = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        User newUser = new User(userId, firstName.trim(), lastName.trim(), timestamp, true, null, null, null);

        try {
            userRepository.save(newUser);
            log.info("Guest user created: {}", newUser.getUserId());
        } catch (Exception e) {
            log.error("Failed to save user to the database: {}", e.getMessage());
            throw new RuntimeException("Failed to save user to the database", e);
        }

        createUserPassenger(newUser);

        return newUser;
    }

    public User createGoogleUser(String firstName, String lastName, String googleId, String googleAccessToken) {
        UUID userId = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        User newUser = new User(userId, firstName.trim(), lastName.trim(), timestamp, false, googleId, null, googleAccessToken);

        try {
            userRepository.save(newUser);
            log.info("Google user created: {}", newUser.getUserId());
        } catch (Exception e) {
            log.error("Failed to save user to the database: {}", e.getMessage());
            throw new RuntimeException("Failed to save user to the database", e);
        }

        createUserPassenger(newUser);

        return newUser;
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new UserNotFoundException("User not found with ID: " + id);
                });
    }

    public Optional<User> getUserByGoogleId(String googleId) {
        return userRepository.findUserByGoogleId(googleId);
    }

    public User updateUserDetails(UUID userId, UpdateUserDetailsRequest request) {
        User existingUser = getUserById(userId);

        request.firstName()
                .filter(name -> !name.isBlank())
                .ifPresent(existingUser::setFirstName);

        request.lastName()
                .filter(name -> !name.isBlank())
                .ifPresent(existingUser::setLastName);

        request.currency()
                .filter(name -> !name.isBlank())
                .ifPresent(existingUser::setCurrency);

        try {
            userRepository.save(existingUser);
            log.info("User details updated for ID: {}", existingUser.getUserId());
        } catch (Exception e) {
            log.error("Failed to update user details: {}", e.getMessage());
            throw new RuntimeException("Failed to save user to the database", e);
        }

        return existingUser;
    }

    public void linkGuestAccountToGoogle(UUID userId, String googleId, String googleAccessToken) {
        Optional<User> googleUser = userRepository.findUserByGoogleId(googleId);

        if (googleUser.isPresent()) {
            log.error("This Google account is already linked to another user, google user ID: {}", googleUser.get().getUserId());
            throw new UserAlreadyExists("This Google account is already linked to another user");
        }

        User existingUser = getUserById(userId);

        existingUser.setGuest(false);
        existingUser.setGoogleId(googleId);
        existingUser.setGoogleToken(googleAccessToken);

        try {
            userRepository.save(existingUser);
            log.info("Google account linked: {}", existingUser.getUserId());
        } catch (Exception e) {
            log.error("Failed to link Google account: {}", e.getMessage());
            throw new RuntimeException("Failed to save user to the database", e);
        }

    }

    public void deleteUser(UUID userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if(!existingUser.isGuest() && existingUser.getGoogleToken() != null) {
            revokeGoogleToken(existingUser.getGoogleToken());
        }

        try {
            userRepository.deleteById(userId);
            log.info("User account deleted: {}", existingUser.getUserId());
        } catch (Exception e) {
            log.error("Failed delete account: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    private void revokeGoogleToken(String googleAccessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://accounts.google.com/o/oauth2/revoke?token=" + googleAccessToken;
        restTemplate.postForEntity(url, null, String.class);
        log.info("Google token revoked");
    }

    private void createUserPassenger(User user) {
        CreatePassengerRequest request = new CreatePassengerRequest(
                user.getFirstName(),
                user.getLastName(),
                "default"
        );
        passengersService.createPassenger(user, request);
    }
}
