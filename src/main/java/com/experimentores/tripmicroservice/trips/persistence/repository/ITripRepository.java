package com.experimentores.tripmicroservice.trips.persistence.repository;

import com.experimentores.tripmicroservice.trips.domain.model.City;
import com.experimentores.tripmicroservice.trips.domain.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ITripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findTripByOriginAndDestinationAndDateAndUserId(City origin, City destination, Date date, Long userId);
    List<Trip> findTripsByUserId(Long userId);
    List<Trip> findTripsByDestination(City destination);
    List<Trip> findTripsByOrigin(City origin);
    List<Trip> findTripsByDateAfter(Date date);
    List<Trip> deleteTripsByUserId(Long userId);
}
