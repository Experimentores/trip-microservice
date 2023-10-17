package com.experimentores.tripmicroservice.trips.mapping;

import com.crudjpa.mapping.IEntityMapper;
import com.experimentores.tripmicroservice.shared.mapping.EnhancedModelMapper;
import com.experimentores.tripmicroservice.trips.domain.model.Trip;
import com.experimentores.tripmicroservice.trips.resources.CreateTripResource;
import com.experimentores.tripmicroservice.trips.resources.TripResource;
import org.springframework.beans.factory.annotation.Autowired;

public class TripMapper implements IEntityMapper<Trip, TripResource, CreateTripResource, Trip> {
    @Autowired
    EnhancedModelMapper mapper;
    @Override
    public Trip fromCreateResourceToModel(CreateTripResource scoreResource) {
        return mapper.map(scoreResource, Trip.class);
    }

    @Override
    public TripResource fromModelToResource(Trip score) {
        return mapper.map(score, TripResource.class);
    }

    @Override
    public Trip fromUpdateResourceToModel(Trip trip) {
        return trip;
    }
}
