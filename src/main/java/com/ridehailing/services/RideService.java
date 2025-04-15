package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Ride;

import java.util.*;

import com.ridehailing.models.Rider;
import com.ridehailing.repository.DriverRepository;
import com.ridehailing.repository.RideRepository;
import com.ridehailing.repository.RiderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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


    public String startRide(Rider rider , Driver driver) {
        if (rider == null) {
            throw new IllegalArgumentException("Invalid Rider");
        }

        Driver selectedDriver = null;

        // Check for preferred driver
        String preferredDriverId = rider.getPreferredDriverId();
        if (preferredDriverId != null) {
            Optional<Driver> preferredDriver = driverRepository.findById(preferredDriverId);
            if (preferredDriver.isPresent() && preferredDriver.get().isAvailable()) {
                selectedDriver = preferredDriver.get();
            }
        }

        // Fallback to default selection logic if preferred driver is not available
        if (selectedDriver == null) {
            selectedDriver = findBestAvailableDriver(rider.getX(), rider.getY());
            if (selectedDriver == null || !selectedDriver.isAvailable()) {
                throw new IllegalArgumentException("No available drivers found");
            }
        }

        // Create ride
        String rideId = UUID.randomUUID().toString();
        Ride ride = new Ride(rideId, rider, selectedDriver);
        ride.setStartX(rider.getX());
        ride.setStartY(rider.getY());
        ride.setEndX(0);
        ride.setEndY(0);
        ride.setTimeTaken(0);
        ride.setActive(true);

        rideRepository.save(ride);

        // Update driver status
        selectedDriver.setAvailable(false);
        driverRepository.save(selectedDriver);

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

    @Transactional
    public void rateDriver(String rideId, int rating, boolean preferred) {
        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isEmpty()) {
            throw new RuntimeException("Ride not found");
        }


        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        Driver driver = ride.get().getDriver();
        int oldTotalRatings = driver.getTotalRatings();
        double currentRating = driver.getRating();

        // Calculate new average rating
        double newRating = ((currentRating * oldTotalRatings) + rating) / (oldTotalRatings + 1);
        driver.setRating(newRating);
        driver.setTotalRatings(oldTotalRatings + 1);
        driverRepository.save(driver);

        // Mark preferred driver if chosen
        if (preferred) {
            Rider rider = ride.get().getRider();
            rider.setPreferredDriverId(driver.getId());
            riderRepository.save(rider);

        }
    }
    public Driver findBestAvailableDriver(int riderX, int riderY) {
        List<Driver> availableDrivers = driverRepository.findByAvailableTrue();

        return availableDrivers.stream()
                .sorted(Comparator
                        .comparingDouble((Driver d) -> calculateDistance(riderX, riderY, d.getX(), d.getY()))
                        .thenComparing(Driver::getRating, Comparator.reverseOrder())
                )
                .findFirst()
                .orElse(null);
    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }


    public Ride getRideById(String rideId) {
        return rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
    }

    public Optional<Driver> getDriverById(String driverId) {
        return driverRepository.findById(driverId);
    }

}
