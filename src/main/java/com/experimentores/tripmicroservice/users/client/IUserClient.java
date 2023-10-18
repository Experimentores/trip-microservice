package com.experimentores.tripmicroservice.users.client;

import com.experimentores.tripmicroservice.users.domain.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="user-service")
public interface IUserClient {
    @GetMapping("{id}")
    User getUserById(@PathVariable Long id);
}
