package com.febfes.fftmback.feign;

import com.febfes.fftmback.config.FeignConfig;
import com.febfes.fftmback.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-client",
        // TODO: const value??
        url = "http://localhost:8092/api",
        configuration = FeignConfig.class,
        path = "/v1/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserDto getUser(@PathVariable Long id);
}
