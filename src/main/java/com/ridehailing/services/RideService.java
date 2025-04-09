package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Ride;
import com.ridehailing.models.Rider;
import com.ridehailing.repository.DriverRepository;
import com.ridehailing.repository.RideRepository;
import com.ridehailing.repository.RiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RideService {

    @Autowired
    private final RideRepository rideRepository;
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;

    public RideService(RideRepository rideRepository, RiderRepository riderRepository, DriverRepository driverRepository) {
        this.rideRepository = rideRepository;
        this.riderRepository = riderRepository;
        this.driverRepository = driverRepository;
    }

    public Optional<Rider> getRiderById(String riderId) {
        return riderRepository.findById(riderId);
    }

    public Optional<Driver> matchDriver(Rider rider) {
        return driverRepository.findAll().stream()
                .filter(Driver::isAvailable)
                .findFirst(); // Find the first available driver
    }

    public String startRide(String rideId, Rider rider, Driver driver) {
        if (rider == null) {
            throw new IllegalArgumentException("Invalid Rider");
        }
        if (rideRepository.existsById(rideId)) {
            throw new IllegalArgumentException("Ride already exists");
        }
        if (driver == null || !driver.isAvailable()) {
            throw new IllegalArgumentException("Invalid Driver");
        }

        Ride ride = new Ride(rideId, rider, driver);

        // Set default values for required fields to avoid null column errors
        ride.setEndX(0);
        ride.setEndY(0);
        ride.setTimeTaken(0);

        rideRepository.save(ride);

        driver.setAvailable(false);
        driverRepository.save(driver);

        return "RIDE_STARTED " + rideId;
    }

    public void stopRide(String rideId, int endX, int endY, int timeTaken) {
        Ride ride = getRideById(rideId);
        Rider rider = ride.getRider();

        ride.stopRide(endX, endY, timeTaken);
        rideRepository.save(ride);

        rider.setNumRides(rider.getNumRides() + 1);
        riderRepository.save(rider);

        // Make the driver available again
        Driver driver = ride.getDriver();
        driver.setAvailable(true);
        driverRepository.save(driver);
    }

    public Ride getRideById(String rideId) {
        return rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
    }
}
