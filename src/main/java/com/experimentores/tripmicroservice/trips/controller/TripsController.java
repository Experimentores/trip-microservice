package com.experimentores.tripmicroservice.trips.controller;

import com.crudjpa.controller.CrudController;
import com.crudjpa.controller.SimpleCrudController;
import com.crudjpa.util.TextDocumentation;
import com.experimentores.tripmicroservice.trips.domain.model.Trip;
import com.experimentores.tripmicroservice.trips.domain.services.ITripService;
import com.experimentores.tripmicroservice.trips.exception.ErrorMessage;
import com.experimentores.tripmicroservice.trips.exception.InvalidCreateResourceException;
import com.experimentores.tripmicroservice.trips.mapping.TripMapper;
import com.experimentores.tripmicroservice.trips.resources.CreateTripResource;
import com.experimentores.tripmicroservice.trips.resources.TripResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tripstore/v1/trips/")
public class TripsController extends CrudController<Trip, Long, TripResource, CreateTripResource, Trip> {
    private final ITripService tripService;
    private final TripMapper tripMapper;

    public TripsController(ITripService tripService, TripMapper tripMapper) {
        super(tripService, tripMapper);
        this.tripService = tripService;
        this.tripMapper = tripMapper;
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trip" + TextDocumentation.FOUND),
            @ApiResponse(responseCode = "404", description = "Trip" + TextDocumentation.NOT_FOUND),
            @ApiResponse(responseCode = "501", description = TextDocumentation.INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<TripResource> getTripById(@PathVariable Long id) throws Exception {
        return getById(id);
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trips" + TextDocumentation.FOUNDS),
            @ApiResponse(responseCode = "404", description = TextDocumentation.NOT_FOUND),
            @ApiResponse(responseCode = "501", description = TextDocumentation.INTERNAL_SERVER_ERROR),
            @ApiResponse(responseCode = "204", description = "Trips" + TextDocumentation.HAVE_NOT_CONTENT)
    })
    public ResponseEntity<List<TripResource>> getAllTrips() {
        return getAll();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripResource> createTrip(@Valid @RequestBody CreateTripResource tripResource, BindingResult result) throws InvalidCreateResourceException {
        if(result.hasErrors()) {
            throw new InvalidCreateResourceException(this.formatMessage(result));
        }
        return insert(tripResource);
    }

    @DeleteMapping(value ="{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripResource> deleteTrip(@PathVariable Long id) {
        return delete(id);
    }

    private String formatMessage(BindingResult result){
        List<Map<String,String>> errors = result.getFieldErrors().stream()
                .map(err -> {
                    Map<String,String> error =  new HashMap<>();
                    error.put(err.getField(), err.getDefaultMessage());
                    return error;

                }).toList();

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(errors);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
