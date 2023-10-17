package com.experimentores.tripmicroservice.trips.mapping;

import com.experimentores.tripmicroservice.shared.mapping.EnhancedModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("behaviourMappingConfiguration")
public class MappingConfiguration {
    @Bean
    public TripMapper tripMapper() {
        return new TripMapper();
    }
}
