package com.ih.itinerary_hub_service.users.service;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final UUID uuidForUser = UUID.randomUUID();
    private final User mockUser = new User(
            uuidForUser,
            "John",
            "Doe",
            LocalDateTime.now(),
            true,
            null,
            null,
            null
    );

    @Nested
    class CreateUser {

        @Test
        void createGuestUser_shouldTrimNames() {
            User guestUser = userService.createGuestUser("  firstName   ", "  lastName");

            assertNotNull(guestUser);
            assertEquals("firstName", guestUser.getFirstName());
            assertEquals("lastName", guestUser.getLastName());
            assertTrue(guestUser.isGuest());
        }

        @Test
        void createGuestUser_whenInvalidRequest_thenThrowsRuntimeException() {
            doThrow(new IllegalArgumentException("Failed to save user to the database")).when(userRepository).save(any());

            assertThrows(RuntimeException.class, () -> userService.createGuestUser("firstName", "lastName"));
        }

        @Test
        void createGoogleUser_shouldTrimNames() {
            User googleUser = userService.createGoogleUser("  firstName   ", "  lastName", "googleId", "googleAccessToken");

            assertNotNull(googleUser);
            assertEquals("firstName", googleUser.getFirstName());
            assertEquals("lastName", googleUser.getLastName());
            assertEquals("googleId", googleUser.getGoogleId());
            assertEquals("googleAccessToken", googleUser.getGoogleToken());
            assertFalse(googleUser.isGuest());
        }

        @Test
        void createGoogleUser_whenInvalidRequest_thenThrowsRuntimeException() {
            doThrow(new IllegalArgumentException("Failed to save user to the database")).when(userRepository).save(any());

            assertThrows(RuntimeException.class, () -> userService.createGoogleUser("firstName", "lastName", "googleId", "googleAccessToken"));
        }
    }

    @Nested
    class GetUser {

        @Test
        void getUserById_shouldReturnUser_whenUserExists() {
            when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));
            assertEquals(userService.getUserById(uuidForUser), mockUser);
        }

        @Test
        void getUserById_shouldThrow_whenUserDoesNotExist() {
            when(userRepository.findById(any())).thenReturn(Optional.empty());
            assertThrows(UserNotFoundException.class, () -> userService.getUserById(uuidForUser));
        }
    }

    @Nested
    class UpdateUser {
        @Test
        void updateUser_shouldUpdateUser_whenUserExists() {
            when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));

            User user = userService.updateUserDetails(uuidForUser, "firstName", "", "GBP");
            assertNotNull(user);
            assertEquals("firstName", user.getFirstName());
            assertEquals("GBP", user.getCurrency());
            assertEquals("Doe", user.getLastName());
        }

        @Test
        void updateUser_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
            when(userRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.updateUserDetails(uuidForUser, "firstName", "lastName", "GBP"));
        }

        @Test
        void updateUser_shouldThrowRuntimeException_whenDbFails() {
            when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));
            when(userRepository.save(any())).thenThrow(new IllegalArgumentException("Failed to save user to the database"));

            assertThrows(RuntimeException.class, () -> userService.updateUserDetails(uuidForUser, "firstName", "", "GBP"));
        }
    }

    @Nested
    class LinkGuestUserToGoogle {
        @Test
        void linkGuestAccountToGoogle_shouldUpdateUser_whenUserExists() {
            when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.linkGuestAccountToGoogle(uuidForUser, "googleId", ""));
        }

        @Test
        void linkGuestAccountToGoogle_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
            when(userRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.linkGuestAccountToGoogle(uuidForUser, "googleId", ""));
        }

        @Test
        void linkGuestAccountToGoogle_shouldThrowRuntimeException_whenDbFails() {
            when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));
            when(userRepository.save(any())).thenThrow(new IllegalArgumentException("Failed to save user to the database"));

            assertThrows(RuntimeException.class, () -> userService.linkGuestAccountToGoogle(uuidForUser, "googleId", ""));
        }
    }

    @Nested
    class DeleteUser {
        @Test
        void deleteUser_shouldUpdateUser_whenUserExists() {
            when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.deleteUser(uuidForUser));
        }

        @Test
        void deleteUser_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
            when(userRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.deleteUser(uuidForUser));
        }

        @Test
        void deleteUser_shouldThrowRuntimeException_whenDbFails() {
            when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));
            doThrow(new IllegalArgumentException("Failed to delete user")).when(userRepository).deleteById(any());

            assertThrows(RuntimeException.class, () -> userService.deleteUser(uuidForUser));
        }
    }

}