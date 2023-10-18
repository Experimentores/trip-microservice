package com.experimentores.tripmicroservice.trips.controller;

import com.crudjpa.controller.CrudController;
import com.crudjpa.util.TextDocumentation;
import com.experimentores.tripmicroservice.trips.domain.model.Trip;
import com.experimentores.tripmicroservice.trips.domain.services.ITripService;
import com.experimentores.tripmicroservice.trips.exception.InvalidCreateResourceException;
import com.experimentores.tripmicroservice.trips.exception.ResourceNotFoundException;
import com.experimentores.tripmicroservice.trips.exception.ValidationException;
import com.experimentores.tripmicroservice.trips.mapping.TripMapper;
import com.experimentores.tripmicroservice.trips.resources.CreateTripResource;
import com.experimentores.tripmicroservice.trips.resources.TripResource;
import com.experimentores.tripmicroservice.users.client.IUserClient;
import com.experimentores.tripmicroservice.users.domain.model.User;
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

@RestController
@RequestMapping("/api/tripstore/v1/trips/")
public class TripsController extends CrudController<Trip, Long, TripResource, CreateTripResource, Trip> {
    private final ITripService tripService;
    private final IUserClient userClient;

    public TripsController(ITripService tripService, TripMapper tripMapper, IUserClient userClient) {
        super(tripService, tripMapper);
        this.tripService = tripService;
        this.userClient = userClient;
    }

    private void validateCreate(CreateTripResource resource) {
        Instant now = Instant.now();
        Instant date = resource.getDate().toInstant();
        if(now.isAfter(date)) {
            throw new ValidationException("Invalid date");
        }
    }

    private Optional<User> getUserFromTrip(Trip resource) {
        try {
            User user = userClient.getUserById(resource.getUserId());
            return  Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private List<TripResource> mapTrips(List<Trip> trips) {
        HashMap<Long, User> users = new HashMap<>();
        return trips.stream().map(trip -> {
            Optional<User> user = Optional.ofNullable(users.getOrDefault(trip.getUserId(), null));
            TripResource resource = mapper.fromModelToResource(trip);
            if(user.isEmpty()) {
                user = getUserFromTrip(trip);
                if (user.isPresent()) {
                    User userResource = user.get();
                    users.put(userResource.getId(), userResource);
                }
            }
            user.ifPresent(resource::setUser);
            return resource;
        }).toList();
    }

    private Trip getTrip(Long id) throws Exception {
        Optional<Trip> trip = tripService.getById(id);
        if(trip.isEmpty())
            throw new ResourceNotFoundException("Trip with id %d not found".formatted(id));
        return trip.get();
    }

    private TripResource getTripResource(Trip trip) {
        TripResource resource = mapper.fromModelToResource(trip);
        Optional<User> user = getUserFromTrip(trip);
        user.ifPresent(resource::setUser);
        return resource;
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trip" + TextDocumentation.FOUND),
            @ApiResponse(responseCode = "404", description = "Trip" + TextDocumentation.NOT_FOUND),
            @ApiResponse(responseCode = "501", description = TextDocumentation.INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<TripResource> getTripById(@PathVariable Long id) throws Exception {
        Trip trip = getTrip(id);
        return ResponseEntity.ok(getTripResource(trip));
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trips" + TextDocumentation.FOUNDS),
            @ApiResponse(responseCode = "404", description = TextDocumentation.NOT_FOUND),
            @ApiResponse(responseCode = "501", description = TextDocumentation.INTERNAL_SERVER_ERROR),
            @ApiResponse(responseCode = "204", description = "Trips" + TextDocumentation.HAVE_NOT_CONTENT)
    })
    public ResponseEntity<List<TripResource>> getAllTrips() {
        try {
            List<Trip> trips = tripService.getAll();
            if(trips.isEmpty())
                return ResponseEntity.noContent().build();
            return ResponseEntity.ok(mapTrips(trips));
        } catch (Exception e) {
            throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.name());
        }
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripResource> createTrip(@Valid @RequestBody CreateTripResource tripResource, BindingResult result) {
        if(result.hasErrors()) {
            throw new InvalidCreateResourceException(this.formatMessage(result));
        }
        validateCreate(tripResource);
        Optional<User> user;
        try {
            user = Optional.ofNullable(userClient.getUserById(tripResource.getUserId()));
            if(user.isEmpty())
                throw new InvalidCreateResourceException("The user id isn't valid");
        } catch (Exception e) {
            throw new InvalidCreateResourceException("The user id isn't valid");
        }

        Optional<Trip> duplicated = tripService.findDuplicated(tripResource.getOrigin(), tripResource.getDestination(), tripResource.getDate(), tripResource.getUserId());
        if(duplicated.isPresent())
            throw new InvalidCreateResourceException("A trip with same values already exists");

        try {
            Trip trip = tripService.save(mapper.fromCreateResourceToModel(tripResource));
            TripResource resource = mapper.fromModelToResource(trip);
            resource.setUser(user.get());
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.name());
        }

    }

    @DeleteMapping(value ="{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripResource> deleteTrip(@PathVariable Long id) throws Exception {
        Trip trip = getTrip(id);
        tripService.delete(id);
        return ResponseEntity.ok(getTripResource(trip));
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

        return ResponseEntity.ok(mapTrips(filteredTrips));
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
