package com.fftmback.authentication.service;

import com.fftmback.authentication.domain.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserEntity getUserById(Long id);
}
