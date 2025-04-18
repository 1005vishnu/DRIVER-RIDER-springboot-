package com.ridehailing.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Rider {
    @Id
    private String id;
    private int x;
    private int y;

    public Rider() {}

    public Rider(String id, int x, int y)
    {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    // Getters and Setters
    public String getId()
    { return id;
    }
    public void setId(String id)
    { this.id = id; }
    public int getX()
    { return x; }
    public void setX(int x)
    { this.x = x; }
    public int getY()
    { return y;
    }
    public void setY(int y)
    { this.y = y;
    }
}
