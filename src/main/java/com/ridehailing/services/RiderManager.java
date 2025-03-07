package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Rider;
import com.ridehailing.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RiderManager {
    private static final double MAX_DISTANCE = 5.0; // Configurable radius (km)
    private final DriverRepository driverRepository;

    public RiderManager(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public List<Driver> matchDrivers(Rider rider) {
        List<Driver> drivers = driverRepository.findAll();
        return drivers.stream()
                .filter(d -> d.isAvailable() && DistanceCalculator.calculateDistance(
                        rider.getX(), rider.getY(), d.getX(), d.getY()) <= MAX_DISTANCE)
                .sorted(Comparator.comparingDouble(
                        d -> DistanceCalculator.calculateDistance(rider.getX(), rider.getY(), d.getX(), d.getY())))
                .limit(5)
                .collect(Collectors.toList());
    }
}
