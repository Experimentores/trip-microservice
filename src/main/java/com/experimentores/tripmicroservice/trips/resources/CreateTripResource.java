package com.experimentores.tripmicroservice.trips.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTripResource {
    @NotNull
    private Long originId;

    @NotNull
    private Long destinationId;

    @NotNull
    private Date date;

    @NotNull
    @Positive(message = "Must be a valid user id")
    private Long userId;
}
