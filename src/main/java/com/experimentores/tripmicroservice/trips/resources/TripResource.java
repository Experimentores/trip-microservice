package com.experimentores.tripmicroservice.trips.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data()
@NoArgsConstructor
@AllArgsConstructor
public class TripResource extends CreateTripResource {
    private Long id;
    private Date date;
}
