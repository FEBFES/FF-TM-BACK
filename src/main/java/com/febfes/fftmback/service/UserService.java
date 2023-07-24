package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.common.specification.UserSpec;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.domain.dao.UserView;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    Long getUserIdByUsername(String username);

    UserEntity getUserById(Long id);

    UserView getUserViewById(Long id);

    void updateUser(UserEntity user, Long id);

    List<UserView> getUsersByFilter(UserSpec userSpec);

    String getUserPicByUserId(Long userId);

    String getUserRole(Long userId, Long projectId);
}
