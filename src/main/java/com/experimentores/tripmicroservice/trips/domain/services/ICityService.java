package com.experimentores.tripmicroservice.trips.domain.services;

import com.crudjpa.service.ICrudService;
import com.experimentores.tripmicroservice.trips.domain.model.City;

import java.util.Optional;

public interface ICityService extends ICrudService<City, Long> {
    Optional<City> findCityByName(String name);
}
