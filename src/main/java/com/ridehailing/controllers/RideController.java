package com.ridehailing.controllers;

import com.ridehailing.models.Driver;
import com.ridehailing.models.Ride;
import com.ridehailing.models.Rider;
import com.ridehailing.services.BillingCalculator;
import com.ridehailing.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/rides")
public class RideController {

    @Autowired
    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startRide(@RequestParam String riderId,
                                            @RequestParam(required = false) String driverId) {
        Optional<Rider> riderOpt = rideService.getRiderById(riderId);
        if (riderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid rider ID");
        }

        Rider rider = riderOpt.get();
        Optional<Driver> selectedDriver = Optional.empty();

        if (driverId != null && !driverId.isBlank()) {
            selectedDriver = rideService.getDriverById(driverId);
            if (selectedDriver.isEmpty() || !selectedDriver.get().isAvailable()) {
                return ResponseEntity.badRequest().body("Provided driver is not available.");
            }
        } else if (rider.getPreferredDriverId() != null) {
            selectedDriver = rideService.getDriverById(rider.getPreferredDriverId());
            if (selectedDriver.isEmpty() || !selectedDriver.get().isAvailable()) {
                selectedDriver = rideService.matchDriver(rider); // fallback to match
            }
        } else {
            selectedDriver = rideService.matchDriver(rider);
        }

        if (selectedDriver.isEmpty()) {
            return ResponseEntity.badRequest().body("No available drivers found.");
        }


        String result = rideService.startRide(rider, selectedDriver.get());
        return ResponseEntity.ok(result);
    }



    @PostMapping("/{rideId}/stop")
    public ResponseEntity<String> stopRide(@PathVariable String rideId, @RequestParam int endX, @RequestParam int endY, @RequestParam int timeTaken)

    {
        rideService.stopRide(rideId, endX, endY, timeTaken);
        return ResponseEntity.ok("Ride stopped successfully.");
    }

    @GetMapping("/{rideId}/bill")
    public ResponseEntity<String> getBill(@PathVariable String rideId)
    {
        Ride ride = rideService.getRideById(rideId);
        double bill = BillingCalculator.calculateBill(ride);
        double discount = ride.getRider().getDiscountPercentage();

        return ResponseEntity.ok("Total Bill: $" + bill + " (Discount Applied: " + discount + "%)");
    }


    @PostMapping("/rate")
    public ResponseEntity<String> rateDriver(@RequestParam String rideId,
                                             @RequestParam int rating,
                                             @RequestParam(required = false, defaultValue = "false") boolean preferred) {
        try {
            rideService.rateDriver(rideId, rating, preferred);
            return ResponseEntity.ok("Rating submitted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error submitting rating.");
        }
    }

}
