package com.experimentores.tripmicroservice.trips.controller;

import com.crudjpa.controller.CrudController;
import com.crudjpa.controller.SimpleCrudController;
import com.crudjpa.util.TextDocumentation;
import com.experimentores.tripmicroservice.trips.domain.model.Trip;
import com.experimentores.tripmicroservice.trips.domain.services.ITripService;
import com.experimentores.tripmicroservice.trips.mapping.TripMapper;
import com.experimentores.tripmicroservice.trips.resources.CreateTripResource;
import com.experimentores.tripmicroservice.trips.resources.TripResource;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public ResponseEntity<TripResource> getTripById(@PathVariable Long id) {
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
    public ResponseEntity<TripResource> createTrip(@Valid @RequestBody CreateTripResource tripResource) {
        return insert(tripResource);
    }

    @DeleteMapping(value ="{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripResource> deleteTrip(@PathVariable Long id) {
        return delete(id);
    }
}
