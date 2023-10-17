package com.experimentores.tripmicroservice.trips.controller;

import com.crudjpa.controller.CrudController;
import com.crudjpa.util.TextDocumentation;
import com.experimentores.tripmicroservice.trips.domain.model.Trip;
import com.experimentores.tripmicroservice.trips.domain.services.ITripService;
import com.experimentores.tripmicroservice.trips.exception.InvalidCreateResourceException;
import com.experimentores.tripmicroservice.trips.exception.ValidationException;
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

import java.time.Instant;
import java.util.*;
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

    private void validateCreate(CreateTripResource resource) {
        Instant now = Instant.now();
        Instant date = resource.getDate().toInstant();
        if(now.isAfter(date)) {
            throw new ValidationException("Invalid date");
        }
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
    public ResponseEntity<TripResource> createTrip(@Valid @RequestBody CreateTripResource tripResource, BindingResult result) {
        if(result.hasErrors()) {
            throw new InvalidCreateResourceException(this.formatMessage(result));
        }
        validateCreate(tripResource);

        Optional<Trip> duplicated = tripService.findDuplicated(tripResource.getOrigin(), tripResource.getDestination(), tripResource.getDate(), tripResource.getUserId());
        if(duplicated.isPresent())
            throw new InvalidCreateResourceException("A trip with same values already exists");

        return insert(tripResource);
    }

    @DeleteMapping(value ="{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripResource> deleteTrip(@PathVariable Long id) {
        return delete(id);
    }

    @GetMapping(value = "search/")
    public ResponseEntity<List<TripResource>> searchTrip(@RequestParam(required = false) Long userId, @RequestParam(required = false) String destination,
                                                         @RequestParam(required = false) String origin, @RequestParam(required = false) String afterThat) {

        Set<Trip> trips = new HashSet<>();
        Date date;

        if (afterThat != null) {
            date = Date.from(Instant.parse(afterThat));
            trips.addAll(tripService.findByDateAfter(date));
        } else {
            date = null;
        }
        if (userId != null) {
            trips.addAll(tripService.findByUserId(userId));
        }
        if (destination != null) {
            trips.addAll(tripService.findByDestination(destination));
        }
        if (origin != null) {
            trips.addAll(tripService.findByOrigin(origin));
        }

        List<Trip> filteredTrips = trips.stream()
                .filter(trip -> (userId == null || trip.getUserId().equals(userId))
                        && (destination == null || trip.getDestination().equals(destination))
                        && (origin == null || trip.getOrigin().equals(origin))
                        && (date == null || trip.getDate().after(date)))
                .toList();


        if(filteredTrips.isEmpty())
            return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);

        return ResponseEntity.ok(filteredTrips.stream().map(tripMapper::fromModelToResource).toList());
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
