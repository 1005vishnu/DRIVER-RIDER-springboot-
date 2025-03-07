package com.ridehailing.controllers;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Rider;
import com.ridehailing.services.RiderManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/riders")
public class RiderController {
    private final RiderManager riderManager;

    public RiderController(RiderManager riderManager) {
        this.riderManager = riderManager;
    }

    @GetMapping("/match")
    public List<Driver> matchDrivers(@RequestParam String riderId,
                                     @RequestParam(defaultValue = "0") int x,
                                     @RequestParam(defaultValue = "0") int y) {
        Rider rider = new Rider(riderId, x, y);
        return riderManager.matchDrivers(rider);
    }
}
