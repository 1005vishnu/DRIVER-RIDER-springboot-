package com.ridehailing.controllers;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Rider;
import com.ridehailing.services.DriverManager;
import com.ridehailing.services.RiderManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final DriverManager driverManager;
    private final RiderManager riderManager;

    public AuthController(DriverManager driverManager, RiderManager riderManager) {
        this.driverManager = driverManager;
        this.riderManager = riderManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String id, @RequestParam String role) {
        Map<String, Object> response = new HashMap<>();
        if (role.equalsIgnoreCase("driver")) {
            Optional<Driver> driver = driverManager.getDriverById(id);
            if (driver.isPresent()) {
                response.put("status", "success");
                response.put("role", "driver");
                response.put("message", "Login successful as driver");
                return ResponseEntity.ok(response);
            }
        } else if (role.equalsIgnoreCase("rider")) {
            Rider rider = riderManager.getRiderById(id);
            if (rider != null) {
                response.put("status", "success");
                response.put("role", "rider");
                response.put("message", "Login successful as rider");
                return ResponseEntity.ok(response);
            }
        }
        response.put("status", "error");
        response.put("message", "User ID not found");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
