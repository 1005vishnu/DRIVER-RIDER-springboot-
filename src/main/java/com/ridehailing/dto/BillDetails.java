package com.ridehailing.dto;

import java.math.BigDecimal;

public class BillDetails {
    private String rideId;
    private String driverId;
    private BigDecimal totalFare;

    public BillDetails(String rideId, String driverId, BigDecimal totalFare) {
        this.rideId = rideId;
        this.driverId = driverId;
        this.totalFare = totalFare;
    }

    public String getRideId() {
        return rideId;
    }

    public String getDriverId() {
        return driverId;
    }

    public BigDecimal getTotalFare() {
        return totalFare;
    }
}
