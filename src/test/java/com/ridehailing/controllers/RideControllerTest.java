package com.ridehailing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehailing.dto.BillDetails;
import com.ridehailing.models.Driver;
import com.ridehailing.models.Ride;
import com.ridehailing.models.Rider;
import com.ridehailing.services.DriverManager;
import com.ridehailing.services.RideManager;
import com.ridehailing.services.RideService;
import com.ridehailing.services.RiderManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RideController.class)
class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RideService rideService;

    @MockBean
    private RiderManager riderManager;

    @MockBean
    private DriverManager driverManager;

    @MockBean
    private RideManager rideManager;

    @Autowired
    private ObjectMapper objectMapper;





    @Test
    void testStartRideValid() throws Exception {
        // Arrange
        Rider rider = new Rider("R1", 0, 0);
        Driver driver = new Driver("D1", 10, 20);
        
        // Properly mock RideService to return valid Rider and Driver
        when(rideService.getRiderById("R1")).thenReturn(Optional.of(rider));
        when(rideService.getDriverById("D1")).thenReturn(Optional.of(driver));
        when(rideService.startRide(rider, driver)).thenReturn("ride123");

        // Act & Assert
        mockMvc.perform(post("/rides/start")
                .param("riderId", "R1")
                .param("driverId", "D1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("ride123"));
    }

    @Test
    void testStartRideInvalid() throws Exception {
        // Arrange
        Rider rider = new Rider("invalidRider", 0, 0);
        when(rideService.getRiderById("invalidRider")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/rides/start")
                .param("riderId", "invalidRider")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid rider ID"));
    }

    @Test
    void testStopRideValid() throws Exception {
        // Arrange
        String rideId = "ride123";
        int endX = 30;
        int endY = 40;
        int timeTaken = 15;
        
        // Properly mock void method
        doNothing().when(rideService).stopRide(rideId, endX, endY, timeTaken);

        // Act & Assert
        mockMvc.perform(post("/rides/ride123/stop")
                .param("endX", String.valueOf(endX))
                .param("endY", String.valueOf(endY))
                .param("timeTaken", String.valueOf(timeTaken))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Ride stopped successfully."));
    }

    @Test
    void testStopRideInvalid() throws Exception {
        // Arrange
        String rideId = "invalidRide";
        int endX = 30;
        int endY = 40;
        int timeTaken = 15;
        // Simulate exception thrown by rideService for invalid ride
        doThrow(new RuntimeException("Ride not found")).when(rideService).stopRide(rideId, endX, endY, timeTaken);

        // Act & Assert
        mockMvc.perform(post("/rides/invalidRide/stop")
                .param("endX", String.valueOf(endX))
                .param("endY", String.valueOf(endY))
                .param("timeTaken", String.valueOf(timeTaken))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGenerateBillValid() throws Exception {
        // Arrange: set up real objects, not mocks
        Rider rider = new Rider("R1", 0, 0);
        rider.setNumRides(0); // No discount
        Driver driver = new Driver("D1", 10, 20);
        Ride ride = new Ride("RIDE1", rider, driver);
        ride.setTimeTaken(10);
        ride.setStartX(0);
        ride.setStartY(0);
        ride.setEndX(10);
        ride.setEndY(10);

        // Mock only the service to return our real ride
        when(rideService.getRideById("RIDE1")).thenReturn(ride);

        // Act & Assert: check that the response contains the expected bill (calculate it manually if needed)
        mockMvc.perform(get("/rides/RIDE1/bill")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.startsWith("Total Bill: $")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Discount Applied: 0.0%")));
    }

    @Test
    void testGenerateBillInvalid() throws Exception {
        // Arrange
        String invalidRideId = "invalidRide";
        // Simulate exception thrown by rideService for invalid ride
        when(rideService.getRideById(invalidRideId)).thenThrow(new RuntimeException("Ride not found"));

        // Act & Assert
        mockMvc.perform(get("/rides/invalidRide/bill")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Ride not found"));
    }
}
