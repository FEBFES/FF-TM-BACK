package com.febfes.fftmback.feign;

import com.febfes.fftmback.config.FeignConfig;
import com.febfes.fftmback.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(name = "user-client",
        url = "${authentication.url}",
        configuration = FeignConfig.class,
        path = "/v1/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserDto getUser(@PathVariable Long id);

    @GetMapping(path = "/list")
    List<UserDto> getUsers(@RequestParam("ids") Set<Long> ids);
}
