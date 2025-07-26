package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.feign.UserClient;
import com.febfes.fftmback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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

    @Override
    public List<UserDto> getUsers(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return userClient.getUsers(ids);
    }
}
