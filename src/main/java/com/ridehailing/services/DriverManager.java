package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.repository.DriverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DriverManager
{
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private final DriverRepository driverRepository;

    public DriverManager(DriverRepository driverRepository)
    {
        this.driverRepository = driverRepository;
    }

    public void addDriver(String id, int x, int y)
    {
        if (id == null || id.trim().isEmpty())
        {
            logger.warn("Attempted to add a driver with an invalid ID.");
            throw new IllegalArgumentException("Invalid Driver ID");
        }
        Driver driver = new Driver(id, x, y);
        driverRepository.save(driver);
        logger.info("Driver {} added at location ({}, {})", id, x, y);
    }

    public List<Driver> getAllDrivers()
    {
        logger.debug("Fetching all drivers...");
        List<Driver> drivers = driverRepository.findAll();
        logger.info("Found {} drivers in the system.", drivers.size());
        return drivers;
    }
}
