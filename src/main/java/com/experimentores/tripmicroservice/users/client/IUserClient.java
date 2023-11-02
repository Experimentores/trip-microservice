package com.experimentores.tripmicroservice.users.client;

import com.experimentores.tripmicroservice.users.domain.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="user-microservice", path = "api/tripstore/v1/users/")
public interface IUserClient {
    @GetMapping("{id}")
    ResponseEntity<User> getUserById(@PathVariable Long id);
}
