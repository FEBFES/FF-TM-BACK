package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.auth.TokenDto;

public interface AuthenticationService {

    void registerUser(UserEntity user);

    TokenDto authenticateUser(UserEntity user);

    void checkAccessTokenExpiration(String token);
}
