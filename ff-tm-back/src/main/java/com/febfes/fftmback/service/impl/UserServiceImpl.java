package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.feign.UserClient;
import com.febfes.fftmback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserClient userClient;

    @Override
    @Cacheable(value = "users", key = "#id", condition = "#id != null")
    public UserDto getUser(Long id) {
        if (id == null) {
            return null;
        }
        return userClient.getUser(id);
    }
}
