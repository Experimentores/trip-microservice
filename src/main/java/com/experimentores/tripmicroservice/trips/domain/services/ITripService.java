package com.experimentores.tripmicroservice.trips.domain.services;

import com.crudjpa.service.ICrudService;
import com.experimentores.tripmicroservice.trips.domain.model.Trip;
import com.experimentores.tripmicroservice.trips.resources.CreateTripResource;

import java.util.Date;
import java.util.Optional;

public interface ITripService extends ICrudService<Trip, Long> {
    Optional<Trip> findDuplicated(String origin, String destination, Date date, Long userId);
}
