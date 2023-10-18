package com.experimentores.tripmicroservice.trips.resources;

import com.experimentores.tripmicroservice.users.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data()
@NoArgsConstructor
@AllArgsConstructor
public class TripResource {
    private Long id;
    private String origin;
    private String destination;
    private Date date;
    private User user;
}
