package com.febfes.fftmback.service;

import com.febfes.fftmback.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {

    UserDto getUser(Long id);

    List<UserDto> getUsers(Set<Long> ids);
}
