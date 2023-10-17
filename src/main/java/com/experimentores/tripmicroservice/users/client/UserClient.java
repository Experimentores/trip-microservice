package com.experimentores.tripmicroservice.users.client;

import com.experimentores.tripmicroservice.users.domain.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "user-service")
@RequestMapping(value = "/api/tripstore/v1/user")
public interface UserClient {
    @GetMapping("/{id}")
    User getUserById(@PathVariable Long id);
}
