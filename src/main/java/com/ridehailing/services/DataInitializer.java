package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Rider;
import com.ridehailing.repository.DriverRepository;
import com.ridehailing.repository.RiderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class DataInitializer
{
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;

    public DataInitializer(DriverRepository driverRepository, RiderRepository riderRepository) {
        this.driverRepository = driverRepository;
        this.riderRepository = riderRepository;
    }

    @PostConstruct
    public void initData()
    {
        logger.info("Initializing data...");
        if (driverRepository.count() == 0)
        {
            logger.info("Adding default drivers...");
            driverRepository.save(new Driver("D1", 1, 1));
            driverRepository.save(new Driver("D2", 3, 4));
            driverRepository.save(new Driver("D3", 5, 6));
        }

        if (riderRepository.count() == 0)
        {
            logger.info("Adding default riders...");
            riderRepository.save(new Rider("R1", 0, 0));
            riderRepository.save(new Rider("R2", 2, 3));
        }
        logger.info("Data initialization completed.");
    }
}

