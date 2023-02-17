package com.febfes.fftmback.service;

import com.febfes.fftmback.dto.auth.RefreshTokenDto;
import com.febfes.fftmback.dto.auth.TokenDto;
import com.febfes.fftmback.dto.auth.UserDetailsDto;

public interface AuthenticationService {

    TokenDto registerUser(UserDetailsDto userDetailsDto);

    RefreshTokenDto authenticateUser(UserDetailsDto userDetailsDto);
}
