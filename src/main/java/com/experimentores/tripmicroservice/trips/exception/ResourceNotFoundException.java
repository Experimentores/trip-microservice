package com.experimentores.tripmicroservice.trips.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException() {

    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
