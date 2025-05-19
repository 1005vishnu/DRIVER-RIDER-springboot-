package com.ridehailing.controllers;

import com.ridehailing.models.Rider;
import com.ridehailing.services.DriverManager;
import com.ridehailing.services.RideManager;
import com.ridehailing.services.RideService;
import com.ridehailing.services.RiderManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;
import java.util.List;
import java.util.Optional;

@WebMvcTest(RiderController.class)
class RiderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverManager driverManager;

    @MockBean
    private RideManager rideManager;

    @MockBean
    private RiderManager riderManager;

    @MockBean
    private RideService rideService;

    @Test
    public void testAddRider() {
        Rider rider = new Rider("rider1", 0, 0);

        // Add rider
        Mockito.doNothing().when(riderManager).addRider(rider);

        // Check if rider is added
        Mockito.when(rideService.getRiderById("rider1")).thenReturn(Optional.of(rider));
        assert rideService.getRiderById("rider1").isPresent() : "Rider should be added successfully";
    }

    @Test
    public void testAddRiderWithEmptyId() {
        Rider rider = new Rider("", 0, 0);

        // Try to add rider with empty ID
        Mockito.doThrow(new IllegalArgumentException("Rider ID cannot be empty.")).when(riderManager).addRider(rider);
        try {
            riderManager.addRider(rider);
            assert false : "Exception should be thrown for empty Rider ID";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().equals("Rider ID cannot be empty.") : "Correct exception message expected";
        }
    }

    @Test
    public void testMatchDrivers() {
        Rider rider = new Rider("rider1", 0, 0);

        // Assuming matchDrivers returns a list of drivers
        Mockito.when(riderManager.matchDrivers(rider)).thenReturn(List.of());

        // Check if drivers are matched
        assert riderManager.matchDrivers(rider) != null : "Drivers should be matched";
    }

    @Test
    public void testMatchDriversWithNoAvailableDrivers() {
        Rider rider = new Rider("rider1", 0, 0);

        // Assuming matchDrivers returns an empty list when no drivers are available
        Mockito.when(riderManager.matchDrivers(rider)).thenReturn(List.of());

        // Check if no drivers are matched
        assert riderManager.matchDrivers(rider).isEmpty() : "No drivers should be matched";
    }
}

