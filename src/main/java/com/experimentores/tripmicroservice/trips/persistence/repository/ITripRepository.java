package com.experimentores.tripmicroservice.trips.persistence.repository;

import com.experimentores.tripmicroservice.trips.domain.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface ITripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findTripByOriginAndDestinationAndDateAndUserId(String origin, String destination, Date date, Long userId);
}
