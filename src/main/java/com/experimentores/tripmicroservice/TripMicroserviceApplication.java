package com.experimentores.tripmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TripMicroserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TripMicroserviceApplication.class, args);
    }

}
