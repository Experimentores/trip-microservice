package com.experimentores.tripmicroservice.trips.mapping;

import com.crudjpa.mapping.IEntityMapper;
import com.experimentores.tripmicroservice.shared.mapping.EnhancedModelMapper;
import com.experimentores.tripmicroservice.trips.domain.model.City;
import com.experimentores.tripmicroservice.trips.resources.CityResource;
import com.experimentores.tripmicroservice.trips.resources.CreateCityResource;
import org.springframework.beans.factory.annotation.Autowired;

public class CityMapper implements IEntityMapper<City, CityResource, CreateCityResource, CreateCityResource> {

    @Autowired
    EnhancedModelMapper mapper;

    @Override
    public City fromCreateResourceToModel(CreateCityResource createCityResource) {
        return mapper.map(createCityResource, City.class);
    }

    @Override
    public void fromCreateResourceToModel(CreateCityResource createCityResource, City city) {
        mapper.map(createCityResource, city);
    }

    @Override
    public CityResource fromModelToResource(City city) {
        return mapper.map(city, CityResource.class);
    }

    @Override
    public City fromUpdateResourceToModel(CreateCityResource createCityResource) {
        return mapper.map(createCityResource, City.class);
    }

    @Override
    public void fromUpdateResourceToModel(CreateCityResource createCityResource, City city) {
        mapper.map(createCityResource, city);
    }
}
