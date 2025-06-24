package com.ridehailing.repository;

import com.ridehailing.models.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiderRepository extends JpaRepository<Rider, String> {
    Optional<Rider> findByEmailAndPassword(String email, String password);
}
