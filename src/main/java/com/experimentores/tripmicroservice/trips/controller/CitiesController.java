package com.experimentores.tripmicroservice.trips.controller;

import com.crudjpa.controller.CrudController;
import com.experimentores.tripmicroservice.trips.domain.model.City;
import com.experimentores.tripmicroservice.trips.domain.services.ICityService;
import com.experimentores.tripmicroservice.trips.exception.InvalidCreateResourceException;
import com.experimentores.tripmicroservice.trips.mapping.CityMapper;
import com.experimentores.tripmicroservice.trips.resources.CityResource;
import com.experimentores.tripmicroservice.trips.resources.CreateCityResource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${tristore.trips-service.cities-path}")
public class CitiesController extends CrudController<City, Long, CityResource, CreateCityResource, CreateCityResource> {
    private final ICityService cityService;

    protected CitiesController(ICityService cityService, CityMapper mapper) {
        super(cityService, mapper);
        this.cityService = cityService;
    }

    @Override
    protected boolean isValidCreateResource(CreateCityResource createCityResource) {

        Optional<City> city = cityService.findCityByName(createCityResource.getName());
        if(city.isPresent())
            throw new InvalidCreateResourceException("A city with this name already exists");

        return true;
    }

    @Override
    protected boolean isValidUpdateResource(CreateCityResource createCityResource) {
        return true;
    }

    @GetMapping
    ResponseEntity<List<CityResource>> findAllCities() {
        return getAll();
    }

    @PostMapping
    ResponseEntity<CityResource> createCity(@Valid @RequestBody CreateCityResource resource, BindingResult result) {
        if(result.hasErrors())
            throw new InvalidCreateResourceException(getErrorsFromResult(result));
        return insert(resource);
    }

    @GetMapping(value = "{id}")
    ResponseEntity<CityResource> findCityById(@PathVariable Long id) {
        return getById(id);
    }

    @DeleteMapping(value = "{id}")
    ResponseEntity<CityResource> deleteCityById(@PathVariable Long id) {
        return delete(id);
    }

    @GetMapping(value = "{name}")
    ResponseEntity<CityResource> findCityByName(@PathVariable String name) {
        Optional<City> city = cityService.findCityByName(name);
        return city.map(value -> ResponseEntity.ok(fromModelToResource(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
