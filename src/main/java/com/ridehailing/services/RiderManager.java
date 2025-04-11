package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Rider;
import com.ridehailing.repository.DriverRepository;
import com.ridehailing.repository.RiderRepository;
import com.ridehailing.services.DistanceCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RiderManager {
    private static final Logger logger = LoggerFactory.getLogger(RiderManager.class);
    private static final double MAX_DISTANCE = 20.0; // Maximum radius in km
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;

    public RiderManager(DriverRepository driverRepository, RiderRepository riderRepository) {
        this.driverRepository = driverRepository;
        this.riderRepository = riderRepository;
    }

    public void addRider(Rider rider) {
        logger.info("Adding rider: {}", rider.getId());
        riderRepository.save(rider);
    }

    public List<Driver> matchDrivers(Rider rider)
    {
        logger.info("Finding available drivers near Rider {} at ({}, {})", rider.getId(), rider.getX(), rider.getY());

        List<Driver> drivers = driverRepository.findAll();
        List<Driver> matchedDrivers = drivers.stream()
                .filter(d -> d.isAvailable() && DistanceCalculator.calculateDistance(
                        rider.getX(), rider.getY(), d.getX(), d.getY()) <= MAX_DISTANCE)
                .sorted(Comparator.comparingDouble(
                        d -> DistanceCalculator.calculateDistance(rider.getX(), rider.getY(), d.getX(), d.getY())))
                .limit(5)
                .collect(Collectors.toList());

        if (matchedDrivers.isEmpty())
        {
            logger.warn("No available drivers found within {} km for Rider {}", MAX_DISTANCE, rider.getId());
        } else
        {
            logger.info("{} drivers matched for Rider {}", matchedDrivers.size(), rider.getId());
        }

        return matchedDrivers;
    }
}
