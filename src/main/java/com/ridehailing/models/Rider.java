package com.ridehailing.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Rider {
    private static final Logger log = LoggerFactory.getLogger(Rider.class);
    @Id
    private String id;
    private int x;
    private int y;
    private String preferredDriver;
    private int numRides;

    public Rider() {}

    public Rider(String id, int x, int y)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.preferredDriver = preferredDriver;
        this.numRides = numRides;


    }

    // Getters and Setters
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    { this.id = id;
    }
    public int getX()
    {
        return x;
    }
    public void setX(int x)
    {
        this.x = x;
    }
    public int getY()
    {
        return y;
    }
    public void setY(int y)
    {
        this.y = y;
    }

    public int getNumRides()
    {
        return numRides;
    }

    public void setNumRides(int numRides)
    {
        this.numRides = numRides;
    }

    public double getDiscountPercentage()
    {
        return numRides >= 5 ? 20.0 : 0.0;
    }
}
