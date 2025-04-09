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
    public ResponseEntity<String> startRide(@RequestParam String rideId,
                                            @RequestParam String riderId)
    {
        Optional<Rider> riderOpt = rideService.getRiderById(riderId);
        if (riderOpt.isEmpty())
        {
            return ResponseEntity.badRequest().body("Invalid rider ID");
        }

        Rider rider = riderOpt.get();
        Optional<Driver> driverOpt = rideService.matchDriver(rider);

        if (driverOpt.isEmpty())
        {
            return ResponseEntity.badRequest().body("No available drivers");
        }

        String result = rideService.startRide(rideId, rider, driverOpt.get());
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
}
