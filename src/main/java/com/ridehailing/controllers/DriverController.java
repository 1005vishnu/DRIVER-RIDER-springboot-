package com.ridehailing.controllers;

import com.ridehailing.models.Driver;
import com.ridehailing.repository.DriverRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverRepository driverRepository;

    public DriverController(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    //  Get all drivers
    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        return ResponseEntity.ok(drivers);
    }

<<<<<<< HEAD
    // Get a single driver by ID
=======
    //  Get a single driver by ID
>>>>>>> e53125a (changes commit)
    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable String id) {
        Optional<Driver> driver = driverRepository.findById(id);
        return driver.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //  Add a new driver
    @PostMapping("/add")
    public ResponseEntity<String> addDriver(@RequestParam String id,
                                            @RequestParam int x,
                                            @RequestParam int y) {
        Driver driver = new Driver(id, x, y);
        driverRepository.save(driver);
        return ResponseEntity.ok("Driver " + id + " added successfully.");
    }

<<<<<<< HEAD
    // 4. Update driver availability
=======
    //  Update driver availability
>>>>>>> e53125a (changes commit)
    @PutMapping("/{id}/availability")
    public ResponseEntity<String> updateAvailability(@PathVariable String id, @RequestParam boolean available) {
        Optional<Driver> driverOpt = driverRepository.findById(id);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            driver.setAvailable(available);
            driverRepository.save(driver);
            return ResponseEntity.ok("Driver " + id + " availability updated.");
        }
        return ResponseEntity.notFound().build();
    }

<<<<<<< HEAD
    //  Delete a driver
=======
    // Delete a driver
>>>>>>> e53125a (changes commit)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable String id) {
        driverRepository.deleteById(id);
        return ResponseEntity.ok("Driver " + id + " deleted successfully.");
    }
}
