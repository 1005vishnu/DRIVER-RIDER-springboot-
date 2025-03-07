package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverManager {
    private final DriverRepository driverRepository;

    public DriverManager(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public void addDriver(String id, int x, int y) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid Driver ID");
        }
        Driver driver = new Driver(id, x, y);
        driverRepository.save(driver);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
}
