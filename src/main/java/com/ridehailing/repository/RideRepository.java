package com.ridehailing.repository;

import com.ridehailing.models.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride, String> {
    @Query("SELECT r FROM Ride r WHERE r.rider.id = :riderId AND r.active = true")
    Ride findActiveRideByRiderId(@Param("riderId") String riderId);
}
