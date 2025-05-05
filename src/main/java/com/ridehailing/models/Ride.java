package com.ridehailing.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Ride {
    @Id
    private String rideId;  // Maps to ride_id in DB

    @Column(name = "startx")  //  column name
    private int startX;

    @Column(name = "starty")  //  column name
    private int startY;

    @Column(name = "endx")
    private Integer endX;

    @Column(name = "endy")
    private Integer endY;

    @Column(name = "time_taken")
    private Integer timeTaken;

    private Boolean active;

    private String preferredDriverId;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;

    public Ride(String rideId, Rider rider, Driver driver) {
        this.rideId = rideId;
        this.rider = rider;
        this.driver = driver;
        this.startX = rider.getX();
        this.startY = rider.getY();
        this.active = true;
    }

    public double calculateDistance() {
        return Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
    }

    public void stopRide(int endX, int endY, int timeTaken) {
        this.endX = endX;
        this.endY = endY;
        this.timeTaken = timeTaken;
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }
}
