package com.ridehailing.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "rider")
@NoArgsConstructor
@Getter
@Setter
public class Rider {
    private static final Logger log = LoggerFactory.getLogger(Rider.class);
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;
    @Column(name = "x", nullable = false)
    private int x;
    @Column(name = "y", nullable = false)
    private int y;
    @Column(name = "preferred_driver")
    private String preferredDriver;
    @Column(name = "discount_percentage")
    private int discountPercentage = 0;
    @Column(name = "num_rides")
    private int numRides = 0;
    @Column(name = "preferred_driver_id")
    private String preferredDriverId;

    // Removed duplicate public Rider() constructor, Lombok's @NoArgsConstructor is sufficient
    public Rider(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public double getDiscountPercentage() {
        return numRides >= 5 ? 20.0 : 0.0;
    }
}
