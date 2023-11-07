package com.experimentores.tripmicroservice.trips.controller;

import com.crudjpa.controller.CrudController;
import com.crudjpa.util.TextDocumentation;
import com.experimentores.tripmicroservice.trips.domain.model.City;
import com.experimentores.tripmicroservice.trips.domain.model.Trip;
import com.experimentores.tripmicroservice.trips.domain.services.ICityService;
import com.experimentores.tripmicroservice.trips.domain.services.ITripService;
import com.experimentores.tripmicroservice.trips.exception.InvalidCreateResourceException;
import com.experimentores.tripmicroservice.trips.exception.ResourceNotFoundException;
import com.experimentores.tripmicroservice.trips.exception.ValidationException;
import com.experimentores.tripmicroservice.trips.mapping.TripMapper;
import com.experimentores.tripmicroservice.trips.resources.CreateTripResource;
import com.experimentores.tripmicroservice.trips.resources.TripResource;
import com.experimentores.tripmicroservice.users.client.IUserClient;
import com.experimentores.tripmicroservice.users.domain.model.User;
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
@RequestMapping("${tripstore.trips-service.path}")
public class TripsController extends CrudController<Trip, Long, TripResource, CreateTripResource, Trip> {
    private final ITripService tripService;
    private final IUserClient userClient;
    private final ICityService cityService;

    public TripsController(ITripService tripService, TripMapper tripMapper, IUserClient userClient, ICityService cityService) {
        super(tripService, tripMapper);
        this.tripService = tripService;
        this.userClient = userClient;
        this.cityService = cityService;
    }

    private void validateCreate(CreateTripResource resource) {
        Instant now = Instant.now();
        Instant date = resource.getDate().toInstant();
        if(now.isAfter(date)) {
            throw new ValidationException("Invalid date");
        }
    }

    private Optional<User> getUserFromId(Long userId) {
        try {
            ResponseEntity<User> response = userClient.getUserById(userId);
            return response.getStatusCode() == HttpStatus.OK ? Optional.ofNullable(response.getBody()) : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private List<TripResource> mapTrips(List<Trip> trips, boolean getUser) {
        HashMap<Long, Optional<User>> users = new HashMap<>();
        return trips.stream().map(trip -> {
            Optional<User> user = users.get(trip.getUserId());
            TripResource resource = mapper.fromModelToResource(trip);
            if(user == null) {
                if(getUser) {
                    user = getUserFromId(trip.getUserId());
                    users.put(trip.getUserId(), user);
                }
                else user = Optional.empty();

            }
            user.ifPresentOrElse(resource::setUser, () -> resource.setUser(null));
            return resource;
        }).toList();
    }

    public List<TripResource> mapTrips(List<Trip> trips) { return mapTrips(trips, true); }

    private Trip getTrip(Long id) {
        Optional<Trip> trip = tripService.getById(id);
        if(trip.isEmpty())
            throw new ResourceNotFoundException("Trip with id %d not found".formatted(id));
        return trip.get();
    }

    private TripResource getTripResource(Trip trip) {
        TripResource resource = mapper.fromModelToResource(trip);
        Optional<User> user = getUserFromId(trip.getUserId());
        user.ifPresentOrElse(resource::setUser, () -> resource.setUser(null));
        return resource;
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trip" + TextDocumentation.FOUND),
            @ApiResponse(responseCode = "404", description = "Trip" + TextDocumentation.NOT_FOUND),
            @ApiResponse(responseCode = "501", description = TextDocumentation.INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<TripResource> getTripById(@PathVariable Long id) {
        Trip trip = getTrip(id);
        return ResponseEntity.ok(getTripResource(trip));
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trips" + TextDocumentation.FOUNDS),
            @ApiResponse(responseCode = "404", description = TextDocumentation.NOT_FOUND),
            @ApiResponse(responseCode = "500", description = TextDocumentation.INTERNAL_SERVER_ERROR),
    })
    public ResponseEntity<List<TripResource>> getAllTrips() {
        try {
            List<Trip> trips = tripService.getAll();
            return ResponseEntity.ok(mapTrips(trips));
        } catch (Exception e) {
            throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.name());
        }
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripResource> createTrip(@Valid @RequestBody CreateTripResource tripResource, BindingResult result) {
        if(result.hasErrors()) {
            throw new InvalidCreateResourceException(getErrorsFromResult(result));
        }
        validateCreate(tripResource);
        Optional<User> user = getUserFromId(tripResource.getUserId());
        if(user.isEmpty())
            throw new InvalidCreateResourceException("The user id isn't valid or users service is down");

        Optional<City> origin = cityService.getById(tripResource.getOriginId());
        if(origin.isEmpty())
            throw new InvalidCreateResourceException("The origin city id is not valid");

        Optional<City> destination = cityService.getById(tripResource.getDestinationId());
        if(destination.isEmpty())
            throw new InvalidCreateResourceException("The destination city id is not valid");

        Optional<Trip> duplicated = tripService.findDuplicated(origin.get(), destination.get(), tripResource.getDate(), tripResource.getUserId());

        if(duplicated.isPresent())
            throw new InvalidCreateResourceException("A trip with same values already exists");

        Trip trip = mapper.fromCreateResourceToModel(tripResource);
        trip.setOrigin(origin.get());
        trip.setDestination(destination.get());
        trip = tripService.save(trip);

        TripResource resource = mapper.fromModelToResource(trip);
        resource.setUser(user.get());
        return ResponseEntity.ok(resource);

    }

    @DeleteMapping(value ="{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripResource> deleteTrip(@PathVariable Long id) {
        Trip trip = getTrip(id);
        tripService.delete(id);
        return ResponseEntity.ok(getTripResource(trip));
    }

    @GetMapping(value = "users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TripResource>> getTripsByUserId(@PathVariable Long id) {
        List<Trip> trips = this.tripService.findByUserId(id);
        List<TripResource> resources = mapTrips(trips, false);
        return resources.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(resources);
    }

    @DeleteMapping("users/{id}")
    public ResponseEntity<List<TripResource>> deleteTripByUserId(@PathVariable Long id) {
        List<Trip> deletedTrips = tripService.deleteTripsByUserId(id);
        List<TripResource> resources = mapTrips(deletedTrips, false);
        return resources.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(resources);
    }

    @GetMapping(value = "search/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TripResource>> searchTrip(@RequestParam(required = false) String destination,
                                                         @RequestParam(required = false) String origin, @RequestParam(required = false) String afterThat) {

        Set<Trip> trips = new HashSet<>();
        Date date;

        if (afterThat != null) {
            date = Date.from(Instant.parse(afterThat));
            trips.addAll(tripService.findByDateAfter(date));
        } else {
            date = null;
        }

        if (destination != null) {
            Optional<City> city = cityService.findCityByName(destination);
            city.ifPresent(value -> trips.addAll(tripService.findByDestination(value)));
        }
        if (origin != null) {
            Optional<City> city = cityService.findCityByName(destination);
            city.ifPresent(value -> trips.addAll(tripService.findByOrigin(value)));
        }

        List<Trip> filteredTrips = trips.stream()
                .filter(trip -> (destination == null || trip.getDestination().getName().equals(destination))
                        && (origin == null || trip.getOrigin().getName().equals(origin))
                        && (date == null || trip.getDate().after(date)))
                .toList();

        return ResponseEntity.ok(mapTrips(filteredTrips));
    }

    @Override
    protected boolean isValidCreateResource(CreateTripResource resource) {
        return true;
    }

    @Override
    protected boolean isValidUpdateResource(Trip trip) {
        return true;
    }

}
