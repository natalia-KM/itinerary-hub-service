package com.ih.itinerary_hub_service.passengers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ih.itinerary_hub_service.integration.BaseIntegrationTest;
import com.ih.itinerary_hub_service.passengers.persistence.repository.ElementPassengerRepository;
import com.ih.itinerary_hub_service.passengers.persistence.repository.PassengersRepository;
import com.ih.itinerary_hub_service.passengers.requests.CreatePassengerRequest;
import com.ih.itinerary_hub_service.passengers.requests.PassengerRequest;
import com.ih.itinerary_hub_service.users.persistence.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PassengerControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ElementPassengerRepository elementPassengerRepository;

    @Autowired
    private PassengersRepository passengersRepository;

    @Nested
    class GetPassengerById {

        @Test
        void getPassengerById() throws Exception {
            String passengerOneResponse = """
                      {
                        "passengerId": "%s",
                        "firstName": "John",
                        "lastName": "Doe",
                        "avatar": "dog"
                      }
                    """.formatted(PASSENGER_ONE);

            mockMvc.perform(get("/v1/passengers/{passengerId}", PASSENGER_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(passengerOneResponse));
        }
    }

    @Nested
    class CreatePassenger {

        @Test
        void createPassenger() throws Exception {
            final CreatePassengerRequest createPassengerRequest = new CreatePassengerRequest(
                    "John",
                    "Doe",
                    "default"
            );

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/passengers")
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassengerRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.passengerId").exists())
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.avatar").value("default"));
        }

        @Test
        void shouldNotCreatePassenger_whenFieldsAreNull() throws Exception {
            final CreatePassengerRequest createPassengerRequest = new CreatePassengerRequest(
                    "John",
                    "Doe",
                    null
            );

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/passengers")
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassengerRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class UpdatePassenger {

        @Test
        void shouldUpdatePassenger_whenPassengerExists() throws Exception {
            PassengerRequest updatePassengerRequest = new PassengerRequest(
                    "Fiona",
                    " ",
                    null
            );

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/passengers/{passengerId}", PASSENGER_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatePassengerRequest)))
                    .andExpect(status().isNoContent());

            String expectedJsonResponse = """
                      {
                        "passengerId": "%s",
                        "firstName": "Fiona",
                        "lastName": "Doe",
                        "avatar": "dog"
                      }
                    """.formatted(PASSENGER_ONE);

            mockMvc.perform(get("/v1/passengers/{passengerId}", PASSENGER_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }
    }

    @Nested
    class DeletePassenger {

        @Test
        void shouldDeletePassenger_whenPassengerExists() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/v1/passengers/{passengerId}", PASSENGER_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/v1/passengers/{passengerId}", PASSENGER_ONE)
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }


    @Nested
    class GetPassengers {

        @Test
        void getPassengers() throws Exception {
            String expectedJsonResponse = """
                    [
                      {
                        "passengerId": "0e85075f-be86-4b31-96ec-08feea54fb0e",
                        "firstName": "John",
                        "lastName": "Doe",
                        "avatar": "dog"
                      },
                      {
                        "passengerId": "3c2c02d3-8a7f-4a1c-94bb-4cce3e9b90c1",
                        "firstName": "Alice",
                        "lastName": "Smith",
                        "avatar": "cat"
                      },
                      {
                        "passengerId": "e0a5409f-6b9e-4f2c-8418-a9275aa4ae52",
                        "firstName": "Bob",
                        "lastName": "Johnson",
                        "avatar": "parrot"
                      },
                      {
                        "passengerId": "d2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f",
                        "firstName": "Clara",
                        "lastName": "Nguyen",
                        "avatar": "fox"
                      },
                      {
                        "passengerId": "ba9d1df2-99b1-4df4-ae00-c5d9ef6e5f57",
                        "firstName": "Ethan",
                        "lastName": "Brown",
                        "avatar": "turtle"
                      }
                    ]
                    """;

            mockMvc.perform(get("/v1/passengers")
                            .cookie(guestUserAccessTokenCookie)
                            .cookie(guestUserIdCookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));
        }
    }
}