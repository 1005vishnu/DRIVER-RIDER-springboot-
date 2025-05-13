package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Rider;
import com.ridehailing.dto.BillDetails;
import com.ridehailing.repository.DriverRepository;
import com.ridehailing.repository.RiderRepository;
import com.ridehailing.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RideServiceTest {

    @Autowired
    private DriverManager driverManager;

    @Autowired
    private RiderManager riderManager;

    @Autowired
    private RideManager rideManager;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private RideRepository rideRepository;

    @BeforeEach
    void cleanDb() {
        rideRepository.deleteAll();
        driverRepository.deleteAll();
        riderRepository.deleteAll();
    }

    @Test
    void testAddDriverAndMatchRider() {
        // Add driver and rider
        driverManager.addDriver("D1", 10, 20);
        riderManager.addRider(new Rider("R1", 10, 20));

        // Match rider with nearby drivers using RiderManager
        Rider rider = riderManager.getRiderById("R1");
        assertNotNull(rider);
        List<Driver> matchedDrivers = riderManager.matchDrivers(rider);
        List<String> matchedDriverIds = matchedDrivers.stream().map(Driver::getId).collect(Collectors.toList());

        assertEquals(1, matchedDriverIds.size());
        assertTrue(matchedDriverIds.contains("D1"));
    }

    @Test
    void testMatchRiderForMoreThan5km() {
        driverManager.addDriver("D1", 10, 20);
        riderManager.addRider(new Rider("R1", 35, 37));

        Rider rider = riderManager.getRiderById("R1");
        assertNotNull(rider);
        List<Driver> matchedDrivers = riderManager.matchDrivers(rider);
        assertTrue(matchedDrivers.isEmpty());
    }


    @Test
    void testStartAndStopRide() {
        driverManager.addDriver("D1", 10, 20);
        riderManager.addRider(new Rider("R1", 10, 20));

        // Start a ride with valid inputs using RideManager
        Rider rider = riderManager.getRiderById("R1");
        assertNotNull(rider);
        Driver driver = driverManager.getDriverById("D1").orElseThrow();
        String startResult = rideManager.startRide("RIDE1", rider, driver);
        String rideId = startResult.replace("RIDE_STARTED ", "");
        assertNotNull(rideId);

        // Stop the ride with valid inputs
        String stoppedRideId = rideManager.stopRide(rideId, 11, 21, 15);
        assertNotNull(stoppedRideId);
    }

    @Test
    void testStartRideInvalidDriverIndex() {
        driverManager.addDriver("D1", 10, 20);
        driverManager.addDriver("D2", 11, 21);
        riderManager.addRider(new Rider("R1", 10, 20));

        Rider rider = riderManager.getRiderById("R1");
        assertNotNull(rider);
        List<Driver> drivers = riderManager.matchDrivers(rider);
        // Try to use invalid index
        Exception exception = assertThrows(
                IndexOutOfBoundsException.class,
                () -> {
                    Driver driver = drivers.get(3); // Invalid index (3)
                    rideManager.startRide("RIDE2", rider, driver);
                }
        );
    }

    @Test
    void testStartRideDuplicateRideId() {
        driverManager.addDriver("D1", 10, 20);
        riderManager.addRider(new Rider("R1", 10, 20));

        Rider rider = riderManager.getRiderById("R1");
        assertNotNull(rider);
        Driver driver = driverManager.getDriverById("D1").orElseThrow();
        String firstResult = rideManager.startRide("UNIQUE_RIDE_ID", rider, driver);
        String rideId = firstResult.replace("RIDE_STARTED ", "");
        assertNotNull(rideId);

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> rideManager.startRide("UNIQUE_RIDE_ID", rider, driver)
        );
        assertEquals("Ride already exists", exception.getMessage());
    }

    @Test
    void testStopRideValid() {
        driverManager.addDriver("D1", 10, 20);
        riderManager.addRider(new Rider("R1", 10, 20));

        Rider rider = riderManager.getRiderById("R1");
        assertNotNull(rider);
        Driver driver = driverManager.getDriverById("D1").orElseThrow();
        String startResult = rideManager.startRide("VALID_RIDE_ID", rider, driver);
        String rideId = startResult.replace("RIDE_STARTED ", "");
        assertNotNull(rideId);

        String stopResult = rideManager.stopRide(rideId, 11, 21, 15);
        assertNotNull(stopResult);
    }

    @Test
    void testStopRideInvalidRideId() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> rideManager.stopRide("INVALID_RIDE_ID", 11, 21, 15)
        );
        assertEquals("INVALID_RIDE", exception.getMessage());
    }

    @Test
    void testStopRideAlreadyCompleted() {
        driverManager.addDriver("D1", 10, 20);
        riderManager.addRider(new Rider("R1", 10, 20));

        Rider rider = riderManager.getRiderById("R1");
        assertNotNull(rider);
        Driver driver = driverManager.getDriverById("D1").orElseThrow();
        String startResult = rideManager.startRide("COMPLETED_RIDE_ID", rider, driver);
        String rideId = startResult.replace("RIDE_STARTED ", "");
        assertNotNull(rideId);

        String firstStopResult = rideManager.stopRide(rideId, 11, 21, 15);
        assertNotNull(firstStopResult);

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> rideManager.stopRide(rideId, 12, 22, 30)
        );
        assertEquals("Ride already stopped", exception.getMessage());
    }

    @Test
    void testBillCalculation() {
        // Arrange
        driverManager.addDriver("D1", 10, 20);
        riderManager.addRider(new Rider("R1", 10, 20));
        Rider rider = riderManager.getRiderById("R1");
        Driver driver = driverManager.getDriverById("D1").orElseThrow();
        String startResult = rideManager.startRide("R1", rider, driver);
        String rideId = startResult.replace("RIDE_STARTED ", "");
        assertNotNull(rideId);
        rideManager.stopRide(rideId, 11, 21, 15);

        // Act
        BigDecimal bill = rideManager.getBill(rideId);

        // Assert
        assertTrue(bill.compareTo(BigDecimal.ZERO) > 0);
    }
}

