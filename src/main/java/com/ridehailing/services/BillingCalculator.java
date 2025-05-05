package com.ridehailing.services;

import com.ridehailing.models.Ride;
import com.ridehailing.models.Rider;
import java.math.BigDecimal;

public class BillingCalculator {

    public static BigDecimal calculateBill(Ride ride)
    {
        BigDecimal baseFare = BigDecimal.valueOf(50); // Base fare
        BigDecimal distanceFare = BigDecimal.valueOf(6.5).multiply(BigDecimal.valueOf(ride.calculateDistance()));
        BigDecimal timeFare = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(ride.getTimeTaken()));
        BigDecimal subtotal = baseFare.add(distanceFare).add(timeFare);
        BigDecimal totalBill = subtotal.multiply(BigDecimal.valueOf(1.2)); // Add 20% service tax

        Rider rider = ride.getRider();
        if (rider.getNumRides() >= 5)
        {
            totalBill = totalBill.multiply(BigDecimal.valueOf(0.8)); // Apply 20% discount
        }

        return totalBill.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
