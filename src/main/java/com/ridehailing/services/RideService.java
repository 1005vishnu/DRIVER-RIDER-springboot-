package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Ride;

import java.util.*;

import com.ridehailing.models.Rider;
import com.ridehailing.repository.DriverRepository;
import com.ridehailing.repository.RideRepository;
import com.ridehailing.repository.RiderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RideService {

    private static final Logger logger = LoggerFactory.getLogger(RideService.class);

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
        logger.debug("Fetching rider with ID: {}", riderId);
        return riderRepository.findById(riderId);
    }

    public Optional<Driver> matchDriver(Rider rider) {
        logger.debug("Matching driver for rider: {}", rider.getId());
        return driverRepository.findAll().stream()
                .filter(Driver::isAvailable)
                .findFirst(); // Find the first available driver
    }

    public String startRide(Rider rider, Driver driver) {
        logger.info("Starting ride for rider: {} with driver: {}", rider.getId(), driver.getId());
        if (rider == null) {
            logger.error("Invalid Rider: null");
            throw new IllegalArgumentException("Invalid Rider");
        }

        Driver selectedDriver = null;

        // Check for preferred driver
        String preferredDriverId = rider.getPreferredDriverId();
        if (preferredDriverId != null) {
            logger.debug("Checking preferred driver: {}", preferredDriverId);
            Optional<Driver> preferredDriver = driverRepository.findById(preferredDriverId);
            if (preferredDriver.isPresent() && preferredDriver.get().isAvailable()) {
                selectedDriver = preferredDriver.get();
                logger.info("Preferred driver selected: {}", selectedDriver.getId());
            }
        }

        // Fallback to default selection logic if preferred driver is not available
        if (selectedDriver == null) {
            logger.debug("Finding best available driver for rider: {}", rider.getId());
            selectedDriver = findBestAvailableDriver(rider.getX(), rider.getY());
            if (selectedDriver == null || !selectedDriver.isAvailable()) {
                logger.warn("No available drivers found for rider: {}", rider.getId());
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
        logger.info("Ride started with ID: {}", rideId);

        // Update driver status
        selectedDriver.setAvailable(false);
        driverRepository.save(selectedDriver);

        return "RIDE_STARTED " + rideId;
    }

    public void stopRide(String rideId, int endX, int endY, int timeTaken) {
        logger.info("Stopping ride with ID: {}", rideId);
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
        logger.info("Ride stopped and driver made available: {}", driver.getId());
    }

    @Transactional
    public void rateDriver(String rideId, int rating, boolean preferred) {
        logger.info("Rating driver for ride ID: {} with rating: {}", rideId, rating);
        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isEmpty()) {
            logger.error("Ride not found for ID: {}", rideId);
            throw new RuntimeException("Ride not found");
        }

        if (rating < 1 || rating > 5) {
            logger.error("Invalid rating: {}. Must be between 1 and 5.", rating);
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
        logger.info("Driver rating updated: {}", driver.getId());

        // Mark preferred driver if chosen
        if (preferred) {
            Rider rider = ride.get().getRider();
            rider.setPreferredDriverId(driver.getId());
            riderRepository.save(rider);
            logger.info("Driver marked as preferred for rider: {}", rider.getId());
        }
    }

    public Driver findBestAvailableDriver(int riderX, int riderY) {
        logger.debug("Finding best available driver near coordinates: ({}, {})", riderX, riderY);
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
        logger.debug("Calculating distance between points: ({}, {}) and ({}, {})", x1, y1, x2, y2);
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public Ride getRideById(String rideId) {
        logger.debug("Fetching ride with ID: {}", rideId);
        return rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
    }

    public Optional<Driver> getDriverById(String driverId) {
        logger.debug("Fetching driver with ID: {}", driverId);
        return driverRepository.findById(driverId);
    }
}

