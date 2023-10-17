package com.experimentores.tripmicroservice.trips.service;

import com.crudjpa.service.impl.CrudService;
import com.experimentores.tripmicroservice.trips.domain.model.Trip;
import com.experimentores.tripmicroservice.trips.domain.services.ITripService;
import com.experimentores.tripmicroservice.trips.persistence.repository.ITripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TripService extends CrudService<Trip, Long> implements ITripService {
    private final ITripRepository tripRepository;
    @Autowired
    public TripService(ITripRepository tripRepository) {
        super(tripRepository);
        this.tripRepository = tripRepository;
    }


    @Override
    public Optional<Trip> findDuplicated(String origin, String destination, Date date, Long userId) {
        return tripRepository.findTripByOriginAndDestinationAndDateAndUserId(origin, destination, date, userId);
    }

    @Override
    public List<Trip> findByUserId(Long userId) {
        return tripRepository.findTripsByUserId(userId);
    }

    @Override
    public List<Trip> findByDestination(String destination) {
        return tripRepository.findTripsByDestination(destination);
    }

    @Override
    public List<Trip> findByOrigin(String origin) {
        return tripRepository.findTripsByOrigin(origin);
    }

    @Override
    public List<Trip> findByDateAfter(Date date) {
        return tripRepository.findTripsByDateAfter(date);
    }


}
