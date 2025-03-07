package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Ride;
import com.ridehailing.models.Rider;
import com.ridehailing.repository.RideRepository;
import org.springframework.stereotype.Service;

@Service
public class RideManager {
    private final RideRepository rideRepository;

    public RideManager(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
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
        rideRepository.save(ride);
        driver.setAvailable(false);
        return "RIDE_STARTED " + rideId;
    }

    public String stopRide(String rideId, int endX, int endY, int timeTaken) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_RIDE"));
        if (!ride.isActive()) {
            throw new IllegalArgumentException("Ride already stopped");
        }
        ride.stopRide(endX, endY, timeTaken);
        rideRepository.save(ride);
        return "RIDE_STOPPED " + rideId;
    }

    public double getBill(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found"));
        return BillingCalculator.calculateBill(ride);
    }
}
