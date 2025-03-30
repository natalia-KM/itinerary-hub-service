package com.ih.itinerary_hub_service.config;

import com.ih.itinerary_hub_service.elements.exceptions.ElementDoesNotExist;
import com.ih.itinerary_hub_service.elements.exceptions.InvalidElementRequest;
import com.ih.itinerary_hub_service.exceptions.DbFailure;
import com.ih.itinerary_hub_service.options.exceptions.CreateOptionInvalidRequest;
import com.ih.itinerary_hub_service.options.exceptions.OptionNotFound;
import com.ih.itinerary_hub_service.sections.exceptions.CreateSectionInvalidRequest;
import com.ih.itinerary_hub_service.sections.exceptions.SectionNotFound;
import com.ih.itinerary_hub_service.trips.exceptions.TripNotFound;
import com.ih.itinerary_hub_service.users.exceptions.UserAlreadyExists;
import com.ih.itinerary_hub_service.users.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidElementRequest.class)
    public ResponseEntity<String> handleInvalidElementRequest(InvalidElementRequest ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ElementDoesNotExist.class)
    public ResponseEntity<String> handleElementNotFound(ElementDoesNotExist ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(OptionNotFound.class)
    public ResponseEntity<String> handleOptionNotFound(OptionNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(CreateOptionInvalidRequest.class)
    public ResponseEntity<String> handleInvalidRequestOnOptionCreation(CreateOptionInvalidRequest ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(SectionNotFound.class)
    public ResponseEntity<String> handleSectionNotFound(SectionNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(CreateSectionInvalidRequest.class)
    public ResponseEntity<String> handleInvalidRequestOnSectionCreation(CreateSectionInvalidRequest ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DbFailure.class)
    public ResponseEntity<String> handleDbFailureOnTrips(DbFailure ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TripNotFound.class)
    public ResponseEntity<String> handleTripNotFound(TripNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<String> handleUserNotFound(UserAlreadyExists ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal Server Error: " + ex.getMessage());
    }
}
