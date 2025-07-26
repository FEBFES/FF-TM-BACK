package com.fftmback.authentication.service;

import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.domain.spec.UserSpec;
import com.fftmback.authentication.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {

    UserEntity getUserById(Long id);

    UserDto getUserDtoById(Long id);

    void updateUser(UserEntity user, Long id);

    List<UserDto> getUsersByFilter(UserSpec userSpec);

    List<UserDto> getUsersByIds(Set<Long> ids);
}
