package com.fftmback.authentication.service;

import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.domain.UserView;
import com.fftmback.authentication.domain.spec.UserSpec;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserEntity getUserById(Long id);

    UserView getUserViewById(Long id);

    void updateUser(UserEntity user, Long id);

    List<UserView> getUsersByFilter(UserSpec userSpec);
}
