package com.experimentores.tripmicroservice.trips.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTripResource {
    @NotBlank
    @NotNull
    private String origin;

    @NotBlank
    @NotNull
    private String destination;
}
