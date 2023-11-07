package com.experimentores.tripmicroservice.trips.persistence.repository;

import com.experimentores.tripmicroservice.trips.domain.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICityRepository extends JpaRepository<City, Long> {
    Optional<City> findCityByName(String name);
}
