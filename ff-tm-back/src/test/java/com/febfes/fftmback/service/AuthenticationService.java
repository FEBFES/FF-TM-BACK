package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.auth.GetAuthDto;

public interface AuthenticationService {

    void registerUser(UserEntity user);

    GetAuthDto authenticateUser(UserEntity user);

    void checkAccessTokenExpiration(String token);
}
