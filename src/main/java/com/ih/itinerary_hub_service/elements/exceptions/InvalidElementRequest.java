package com.ih.itinerary_hub_service.elements.exceptions;

public class InvalidElementRequest extends RuntimeException {
  public InvalidElementRequest(String message) {
    super(message);
  }
}
