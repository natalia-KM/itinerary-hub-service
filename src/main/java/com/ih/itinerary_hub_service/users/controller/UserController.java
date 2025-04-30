package com.ih.itinerary_hub_service.users.controller;

import com.ih.itinerary_hub_service.config.CookieMaker;
import com.ih.itinerary_hub_service.users.auth.JwtService;
import com.ih.itinerary_hub_service.users.persistence.entity.User;
import com.ih.itinerary_hub_service.users.requests.UpdateUserDetailsRequest;
import com.ih.itinerary_hub_service.users.requests.UserDetailsRequest;
import com.ih.itinerary_hub_service.users.responses.GetUserDetailsResponse;
import com.ih.itinerary_hub_service.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1")
@Slf4j
public class UserController {

    private static final String RESTRICTED_PATH = "/users";
    private static final String CREATE_USER_PATH = "/users/guest";

    private final UserService userService;
    private final JwtService jwtService;
    private final CookieMaker cookieMaker;

    @Autowired
    public UserController(UserService userService, JwtService jwtService, CookieMaker cookieMaker) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.cookieMaker = cookieMaker;
    }

    @PostMapping(CREATE_USER_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "${users.createGuestUser.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Guest user created")})
    public void createUser(@Valid @RequestBody UserDetailsRequest request, HttpServletResponse response) {
        User user = userService.createGuestUser(request.firstName(), request.lastName());
        log.info("Guest user created: {}", user);

        UUID uuid = UUID.randomUUID();
        String accessToken = jwtService.generateAccessToken("guest-user-" + uuid, user.getUserId());
        cookieMaker.addDefaultCookies(response, user.getUserId().toString(), accessToken);
    }

    @GetMapping(RESTRICTED_PATH)
    @Operation(summary = "${users.getUserDetails.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User details retrieved")})
    public GetUserDetailsResponse getUserDetails(@RequestAttribute("userId") UUID userId) {
        User user = userService.getUserById(userId);

        return new GetUserDetailsResponse(
                user.getFirstName(),
                user.getLastName(),
                user.isGuest(),
                user.getCreatedAt(),
                Optional.ofNullable(user.getCurrency())
        );
    }

    @PutMapping(RESTRICTED_PATH)
    @Operation(summary = "${users.updateUserDetails.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User details updated")})
    public GetUserDetailsResponse updateUserDetails(@RequestAttribute("userId") UUID userId, @RequestBody UpdateUserDetailsRequest request) {
        User user = userService.updateUserDetails(userId, request);

        return new GetUserDetailsResponse(
                user.getFirstName(),
                user.getLastName(),
                user.isGuest(),
                user.getCreatedAt(),
                Optional.ofNullable(user.getCurrency())
        );
    }

    @DeleteMapping(RESTRICTED_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "${users.deleteUser.summary}")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "User deleted")})
    public void deleteUser(@RequestAttribute("userId") UUID userId, HttpServletResponse response) {
        userService.deleteUser(userId);
        cookieMaker.removeDefaultCookies(response);
    }
}
