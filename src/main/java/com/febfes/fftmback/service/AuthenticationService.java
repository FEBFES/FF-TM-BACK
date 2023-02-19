package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.UserEntity;
import com.febfes.fftmback.dto.auth.RefreshTokenDto;
import com.febfes.fftmback.dto.auth.TokenDto;

public interface AuthenticationService {

    TokenDto registerUser(UserEntity user);

    RefreshTokenDto authenticateUser(UserEntity user);

    boolean hasTokenExpired(String token);
}
