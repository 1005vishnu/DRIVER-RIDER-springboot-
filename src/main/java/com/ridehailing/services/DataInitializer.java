package com.ridehailing.services;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Rider;
import com.ridehailing.repository.DriverRepository;
import com.ridehailing.repository.RiderRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class DataInitializer {

    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;

    public DataInitializer(DriverRepository driverRepository, RiderRepository riderRepository) {
        this.driverRepository = driverRepository;
        this.riderRepository = riderRepository;
    }

    @PostConstruct
    public void initData() {
        if (driverRepository.count() == 0) {
            driverRepository.save(new Driver("D1", 1, 1));
            driverRepository.save(new Driver("D2", 3, 4));
            driverRepository.save(new Driver("D3", 5, 6));
        }

        if (riderRepository.count() == 0) {
            riderRepository.save(new Rider("R1", 0, 0));
            riderRepository.save(new Rider("R2", 2, 3));
        }
    }
}
