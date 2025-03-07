package com.ridehailing.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Ride {
    @Id
    private String rideId;

    @ManyToOne
    private Rider rider;

    @ManyToOne
    private Driver driver;

    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private int timeTaken; // in minutes
    private boolean active = true;

    public Ride() {}

    public Ride(String rideId, Rider rider, Driver driver) {
        this.rideId = rideId;
        this.rider = rider;
        this.driver = driver;
        this.startX = rider.getX();
        this.startY = rider.getY();
        this.active = true;
    }

    // Getters and Setters
    public String getRideId() { return rideId; }
    public void setRideId(String rideId) { this.rideId = rideId; }
    public Rider getRider() { return rider; }
    public void setRider(Rider rider) { this.rider = rider; }
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
    public int getStartX() { return startX; }
    public void setStartX(int startX) { this.startX = startX; }
    public int getStartY() { return startY; }
    public void setStartY(int startY) { this.startY = startY; }
    public int getEndX() { return endX; }
    public void setEndX(int endX) { this.endX = endX; }
    public int getEndY() { return endY; }
    public void setEndY(int endY) { this.endY = endY; }
    public int getTimeTaken() { return timeTaken; }
    public void setTimeTaken(int timeTaken) { this.timeTaken = timeTaken; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public double calculateDistance() {
        return Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
    }

    public void stopRide(int endX, int endY, int timeTaken) {
        this.endX = endX;
        this.endY = endY;
        this.timeTaken = timeTaken;
        this.active = false;
    }
}
