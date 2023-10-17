package com.experimentores.tripmicroservice.trips.persistence.repository;

import com.experimentores.tripmicroservice.trips.domain.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ITripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findTripByOriginAndDestinationAndDateAndUserId(String origin, String destination, Date date, Long userId);
    List<Trip> findTripsByUserId(Long userId);
    List<Trip> findTripsByDestination(String destination);
    List<Trip> findTripsByOrigin(String origin);
    List<Trip> findTripsByDateAfter(Date date);
}
