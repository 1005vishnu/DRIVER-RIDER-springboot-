package com.ridehailing.controllers;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Rider;
import com.ridehailing.services.RiderManager;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/riders")
public class RiderController {
    private final RiderManager riderManager;

    public RiderController(RiderManager riderManager) {
        this.riderManager = riderManager;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addRider(@Valid @RequestBody Rider rider) {
        if (rider.getId() == null || rider.getId().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rider ID cannot be empty.");
        }

        try {
            if (riderManager.getRiderByEmail(rider.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
            }
            riderManager.addRider(rider.getId(), rider.getX(), rider.getY(), rider.getEmail(), rider.getPassword());
            return ResponseEntity.ok("Rider added successfully.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add rider.", e);
        }
    }

    @GetMapping("/match")
    public ResponseEntity<?> matchDrivers(@RequestParam String riderId,
                                          @RequestParam(defaultValue = "0") int x,
                                          @RequestParam(defaultValue = "0") int y) {
        if (riderId == null || riderId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rider ID cannot be empty.");
        }

        try {
            Rider rider = new Rider(riderId, x, y);
            List<Driver> drivers = riderManager.matchDrivers(rider);

            if (drivers.isEmpty()) {
                return ResponseEntity.ok("No matching drivers found.");
            }

            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error matching drivers.", e);
        }
    }
}
