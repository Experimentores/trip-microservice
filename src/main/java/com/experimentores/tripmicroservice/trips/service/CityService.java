package com.experimentores.tripmicroservice.trips.service;

import com.crudjpa.service.impl.CrudService;
import com.experimentores.tripmicroservice.trips.domain.model.City;
import com.experimentores.tripmicroservice.trips.domain.services.ICityService;
import com.experimentores.tripmicroservice.trips.persistence.repository.ICityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CityService extends CrudService<City, Long> implements ICityService {
    private final ICityRepository cityRepository;
    public CityService(ICityRepository repository) {
        super(repository);
        cityRepository = repository;
    }

    @Override
    public Optional<City> findCityByName(String name) {
        return cityRepository.findCityByName(name);
    }
}
