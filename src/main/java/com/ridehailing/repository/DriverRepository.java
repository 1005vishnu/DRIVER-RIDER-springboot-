package com.ridehailing.repository;

import com.ridehailing.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {

    List<Driver> findByAvailableTrue();

    Optional<Driver> findByEmailAndPassword(String email, String password);
}
