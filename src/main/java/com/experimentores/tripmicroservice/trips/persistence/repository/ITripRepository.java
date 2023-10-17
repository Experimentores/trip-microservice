package com.experimentores.tripmicroservice.trips.persistence.repository;

import com.experimentores.tripmicroservice.trips.domain.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITripRepository extends JpaRepository<Trip, Long> {
}
