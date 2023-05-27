package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    Long getUserIdByUsername(String username);

    UserEntity getUserById(Long id);

    void updateUser(UserEntity user, Long id);

    List<UserEntity> getUsersByFilter(String filter);
}
