package com.ridehailing.controllers;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Rider;
import com.ridehailing.services.RiderManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/riders")
public class RiderController {
    private final RiderManager riderManager;

    public RiderController(RiderManager riderManager) {
        this.riderManager = riderManager;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addRider(@RequestBody Rider rider)
    {
        if (rider.getId() == null || rider.getId().trim().isEmpty())
        {
            return ResponseEntity.badRequest().body("Rider ID cannot be empty.");
        }

        riderManager.addRider(rider);
        return ResponseEntity.ok("Rider added successfully.");
    }

    @GetMapping("/match")
    public ResponseEntity<?> matchDrivers(@RequestParam String riderId,
                                          @RequestParam(defaultValue = "0") int x,
                                          @RequestParam(defaultValue = "0") int y) {
        if (riderId == null || riderId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Rider ID cannot be empty.");
        }

        Rider rider = new Rider(riderId, x, y);
        List<Driver> drivers = riderManager.matchDrivers(rider);

        if (drivers.isEmpty()) {
            return ResponseEntity.ok("No matching drivers found.");
        }

        return ResponseEntity.ok(drivers);
    }

}
