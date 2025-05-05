package com.ridehailing.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Rider {
    private static final Logger log = LoggerFactory.getLogger(Rider.class);
    @Id
    private String id;
    private int x;
    private int y;
    private String preferredDriverId;
    private int numRides;

    public Rider(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public double getDiscountPercentage() {
        return numRides >= 5 ? 20.0 : 0.0;
    }
}
