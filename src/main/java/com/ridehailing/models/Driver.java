package com.ridehailing.models;

import jakarta.persistence.*;

@Entity
@Table(name = "drivers") // Explicit table mapping
public class Driver {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "x_coordinate", nullable = false)
    private int x;

    @Column(name = "y_coordinate", nullable = false)
    private int y;

    @Column(name = "available", nullable = false)
    private boolean available = true;

    public Driver() {}

    public Driver(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.available = true;
    }

    // Getters and Setters
    public String getId()
    { return id;
    }
    public void setId(String id)

    { this.id = id;
    }
    public int getX()
    { return x; }
    public void setX(int x)
    { this.x = x; }
    public int getY()
    { return y; }
    public void setY(int y)
    { this.y = y; }
    public boolean isAvailable()
    { return available; }
    public void setAvailable(boolean available)
    { this.available = available; }
}

