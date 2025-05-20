package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Rider;
import com.ridehailing.models.Ride;
import com.ridehailing.dto.BillDetails;
import com.ridehailing.repository.DriverRepository;
import com.ridehailing.repository.RiderRepository;
import com.ridehailing.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class RideServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RiderRepository riderRepository;

    @Mock
    private RideRepository rideRepository;

    @InjectMocks
    private DriverManager driverManager;

    @InjectMocks
    private RiderManager riderManager;

    @InjectMocks
    private RideManager rideManager;

    private Driver driver1;
    private Driver driver2;
    private Rider rider1;
    private Rider rider2;
    private Ride ride1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test data
        driver1 = new Driver("driver1", 0, 0);
        driver1.setAvailable(true);
        driver1.setRating(4.5);
        
        driver2 = new Driver("driver2", 10, 10);
        driver2.setAvailable(true);
        driver2.setRating(4.8);
        
        rider1 = new Rider("rider1", 1, 1);
        rider2 = new Rider("rider2", 8, 8);
        
        ride1 = new Ride("ride1", rider1, driver1);
        
        // Setup mock repository behavior
        when(driverRepository.findById("driver1")).thenReturn(Optional.of(driver1));
        when(driverRepository.findById("driver2")).thenReturn(Optional.of(driver2));
        when(driverRepository.findAll()).thenReturn(Arrays.asList(driver1, driver2));
        
        when(riderRepository.findById("rider1")).thenReturn(Optional.of(rider1));
        when(riderRepository.findById("rider2")).thenReturn(Optional.of(rider2));
        
        when(rideRepository.findById("ride1")).thenReturn(Optional.of(ride1));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testAddDriverAndMatchRider() {
        // Test adding a driver
        driverManager.addDriver("driver3", 5, 5);
        verify(driverRepository).save(any(Driver.class));
        
        // Test matching a rider with drivers
        when(riderRepository.findById("rider1")).thenReturn(Optional.of(rider1));
        List<Driver> availableDrivers = Arrays.asList(driver1, driver2);
        when(driverRepository.findAll()).thenReturn(availableDrivers);
        
        Rider rider = riderManager.getRiderById("rider1");
        assertNotNull(rider);
        List<Driver> matchedDrivers = riderManager.matchDrivers(rider);
        assertFalse(matchedDrivers.isEmpty());
        assertEquals("driver1", matchedDrivers.get(0).getId()); // Should match the closest driver
    }

    @Test
    void testMatchRiderForMoreThan5km() {
        // Create a rider far from any driver
        Rider farRider = new Rider("farRider", 50, 50);
        when(riderRepository.findById("farRider")).thenReturn(Optional.of(farRider));
        
        // Test matching a rider that's too far
        List<Driver> availableDrivers = Arrays.asList(driver1, driver2);
        when(driverRepository.findAll()).thenReturn(availableDrivers);
        
        Rider rider = riderManager.getRiderById("farRider");
        assertNotNull(rider);
        List<Driver> matchedDrivers = riderManager.matchDrivers(rider);
        assertTrue(matchedDrivers.isEmpty()); // Should not match any driver beyond MAX_DISTANCE
    }

    @Test
    void testStartAndStopRide() {
        // Setup
        String rideId = "testRide123";
        when(rideRepository.existsById(rideId)).thenReturn(false);
        
        // Start ride
        String startResult = rideManager.startRide(rideId, rider1, driver1);
        assertNotNull(startResult);
        assertTrue(startResult.contains(rideId));
        verify(rideRepository).save(any(Ride.class));
        
        // Setup for stopping the ride
        Ride activeRide = new Ride(rideId, rider1, driver1);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(activeRide));
        
        // Stop ride
        String stopResult = rideManager.stopRide(rideId, 5, 5, 15);
        assertNotNull(stopResult);
        assertTrue(stopResult.contains(rideId));
        verify(rideRepository, times(2)).save(any(Ride.class));
    }

    @Test
    void testStartRideInvalidDriverIndex() {
        // Setup - driver not available
        Driver unavailableDriver = new Driver("unavailableDriver", 3, 3);
        unavailableDriver.setAvailable(false);
        when(driverRepository.findById("unavailableDriver")).thenReturn(Optional.of(unavailableDriver));
        
        // Should throw exception when driver is not available
        assertThrows(IllegalArgumentException.class, () -> {
            rideManager.startRide("testRide", rider1, unavailableDriver);
        });
    }

    @Test
    void testStartRideDuplicateRideId() {
        // Setup - ride already exists
        String duplicateRideId = "duplicateRide";
        when(rideRepository.existsById(duplicateRideId)).thenReturn(true);
        
        // Should throw exception when ride ID already exists
        assertThrows(IllegalArgumentException.class, () -> {
            rideManager.startRide(duplicateRideId, rider1, driver1);
        });
    }

    @Test
    void testStopRideValid() {
        // Create an active ride
        String rideId = "activeRide";
        Ride activeRide = new Ride(rideId, rider1, driver1);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(activeRide));
        
        // Stop the ride
        String result = rideManager.stopRide(rideId, 10, 10, 20);
        assertNotNull(result);
        assertTrue(result.contains(rideId));
        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    void testStopRideInvalidRideId() {
        // Setup - ride doesn't exist
        when(rideRepository.findById("nonexistentRide")).thenReturn(Optional.empty());
        
        // Should throw exception when ride doesn't exist
        assertThrows(IllegalArgumentException.class, () -> {
            rideManager.stopRide("nonexistentRide", 5, 5, 10);
        });
    }

    @Test
    void testStopRideAlreadyCompleted() {
        // Create a completed ride
        String rideId = "completedRide";
        Ride completedRide = new Ride(rideId, rider1, driver1);
        completedRide.stopRide(10, 10, 15); // Mark as completed
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(completedRide));
        
        // Should throw exception when ride is already completed
        assertThrows(IllegalArgumentException.class, () -> {
            rideManager.stopRide(rideId, 15, 15, 20);
        });
    }

    @Test
    void testBillCalculation() {
        // Create a completed ride
        String rideId = "billTestRide";
        Ride completedRide = new Ride(rideId, rider1, driver1);
        completedRide.stopRide(10, 10, 30); // 30 minutes ride
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(completedRide));
        
        // Calculate bill
        BigDecimal bill = rideManager.getBill(rideId);
        assertNotNull(bill);
        assertTrue(bill.compareTo(BigDecimal.ZERO) > 0);
    }
}
