package com.fftmback.authentication.service;


import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.dto.ConnValidationResponse;
import com.fftmback.authentication.dto.GetAuthDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {

    void registerUser(UserEntity user);

    GetAuthDto authenticateUser(UserEntity user);

    void checkAccessTokenExpiration(String token);

    ConnValidationResponse validateToken(HttpServletRequest request);
}
