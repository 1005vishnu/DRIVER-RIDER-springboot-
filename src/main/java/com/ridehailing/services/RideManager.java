package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Rider;
import com.ridehailing.models.Ride;
import com.ridehailing.repository.RideRepository;
import com.ridehailing.services.BillingCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RideManager
{
    private static final Logger logger = LoggerFactory.getLogger(RideManager.class);
    private final RideRepository rideRepository;

    public RideManager(RideRepository rideRepository)
    {
        this.rideRepository = rideRepository;
    }

    public String startRide(String rideId, Rider rider, Driver driver)

    {
        logger.info("Attempting to start ride: {}", rideId);

        if (rider == null)
        {
            logger.warn("Ride {} failed: Invalid Rider", rideId);
            throw new IllegalArgumentException("Invalid Rider");
        }
        if (rideRepository.existsById(rideId))
        {
            logger.warn("Ride {} already exists", rideId);
            throw new IllegalArgumentException("Ride already exists");
        }
        if (driver == null || !driver.isAvailable())
        {
            logger.warn("Ride {} failed: Driver {} is unavailable or invalid", rideId, driver.getId());
            throw new IllegalArgumentException("Invalid Driver");
        }

        Ride ride = new Ride(rideId, rider, driver);
        rideRepository.save(ride);
        driver.setAvailable(false);
        logger.info("Ride {} started successfully with Driver {} and Rider {}", rideId, driver.getId(), rider.getId());
        return "RIDE_STARTED " + rideId;
    }

    public String stopRide(String rideId, int endX, int endY, int timeTaken)
    {
        logger.info("Stopping ride {}", rideId);

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> {
                    logger.error("Ride {} not found!", rideId);
                    return new IllegalArgumentException("INVALID_RIDE");
                });

        if (!ride.isActive())
        {
            logger.warn("Ride {} is already stopped.", rideId);
            throw new IllegalArgumentException("Ride already stopped");
        }

        ride.stopRide(endX, endY, timeTaken);
        rideRepository.save(ride);
        logger.info("Ride {} stopped. End location: ({}, {}), Time taken: {} minutes", rideId, endX, endY, timeTaken);
        return "RIDE_STOPPED " + rideId;
    }

    public double getBill(String rideId)
    {
        logger.info("Calculating bill for ride {}", rideId);

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> {
                    logger.error("Ride {} not found!", rideId);
                    return new IllegalArgumentException("Ride not found");
                });

        double bill = BillingCalculator.calculateBill(ride);
        logger.info("Total bill for ride {}: ${}", rideId, bill);
        return bill;
    }
}
