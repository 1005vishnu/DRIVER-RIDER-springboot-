package com.ridehailing.models;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drivers") // Explicit table mapping
@NoArgsConstructor
@Getter
@Setter
public class Driver {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "x_coordinate", nullable = false)
    private int x;

    @Column(name = "y_coordinate", nullable = false)
    private int y;
    @Column
    private double rating = 5.0;

    @Column(name = "total_ratings")
    private int totalRatings = 0;

    @Column(name = "available", nullable = false)
    private boolean available = true;

    public Driver(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.available = true;
    }

}
