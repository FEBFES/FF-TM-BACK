package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Long getUserIdByUsername(String username);

    UserEntity getUserById(Long id);

    void updateUser(UserEntity user, Long id);
}
