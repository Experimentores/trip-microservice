package com.experimentores.tripmicroservice.trips.domain.services;

import com.crudjpa.service.ICrudService;
import com.experimentores.tripmicroservice.trips.domain.model.City;
import com.experimentores.tripmicroservice.trips.domain.model.Trip;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ITripService extends ICrudService<Trip, Long> {
    Optional<Trip> findDuplicated(City origin, City destination, Date date, Long userId);
    List<Trip> findByUserId(Long userId);
    List<Trip> findByDestination(City destination);
    List<Trip> findByOrigin(City origin);
    List<Trip> findByDateAfter(Date date);
    List<Trip> deleteTripsByUserId(Long userId);
}
