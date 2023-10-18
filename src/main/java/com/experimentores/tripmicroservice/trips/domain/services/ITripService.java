package com.experimentores.tripmicroservice.trips.domain.services;

import com.crudjpa.service.ICrudService;
import com.experimentores.tripmicroservice.trips.domain.model.Trip;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ITripService extends ICrudService<Trip, Long> {
    Optional<Trip> findDuplicated(String origin, String destination, Date date, Long userId);
    List<Trip> findByUserId(Long userId);
    List<Trip> findByDestination(String destination);
    List<Trip> findByOrigin(String origin);
    List<Trip> findByDateAfter(Date date);
    List<Trip> deleteTripsByUserId(Long userId);
}
