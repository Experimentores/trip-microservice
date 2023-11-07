package com.experimentores.tripmicroservice.trips.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityResource {
    private Long id;
    private String name;
}
