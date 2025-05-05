package com.ridehailing.controllers;

import com.ridehailing.models.Driver;
import com.ridehailing.services.DriverManager;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverManager driverManager;

    public DriverController(DriverManager driverManager) {
        this.driverManager = driverManager;
    }

    //  Get all drivers
    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        List<Driver> drivers = driverManager.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }


    // Get a single driver by ID

    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable String id) {
        try {
            Optional<Driver> driver = driverManager.getDriverById(id);
            if (driver.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found");
            }
            return ResponseEntity.ok(driver.get());
        } catch (ResponseStatusException e) {
            throw e; // Re-throw response status exceptions as is
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving driver.", e);
        }
    }

    //  Add a new driver
    @PostMapping("/add")
    public ResponseEntity<String> addDriver(@Valid @RequestBody Driver driver) {
        try {
            driverManager.addDriver(driver.getId(), driver.getX(), driver.getY());
            return ResponseEntity.ok("Driver " + driver.getId() + " added successfully.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add driver.", e);
        }
    }

    // Update driver availability

    @PutMapping("/{id}/availability")
    public ResponseEntity<String> updateAvailability(@PathVariable String id, @RequestParam boolean available) {
        try {
            boolean updated = driverManager.updateAvailability(id, available);
            if (updated) {
                return ResponseEntity.ok("Driver availability updated.");
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found");
            }
        } catch (ResponseStatusException e) {
            throw e; // Re-throw response status exceptions as is
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating availability.", e);
        }
    }

    //  Delete a driver

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable String id) {
        try {
            driverManager.deleteDriver(id);
            return ResponseEntity.ok("Driver " + id + " deleted successfully.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting driver.", e);
        }
    }
}
