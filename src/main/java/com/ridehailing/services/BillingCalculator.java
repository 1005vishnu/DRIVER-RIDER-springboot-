package com.ridehailing.services;

import com.ridehailing.models.Ride;
import com.ridehailing.models.Rider;

public class BillingCalculator {

    public static double calculateBill(Ride ride)
    {
        double baseFare = 50; // Base fare
        double distanceFare = 6.5 * ride.calculateDistance();
        double timeFare = 2 * ride.getTimeTaken();
        double subtotal = baseFare + distanceFare + timeFare;
        double totalBill = subtotal * 1.2; // Add 20% service tax

        Rider rider = ride.getRider();
        if (rider.getNumRides() >= 5)
        {
            totalBill *= 0.8; // Apply 20% discount
        }

        return totalBill;
    }
}
