package com.febfes.fftmback.service;

import com.febfes.fftmback.dto.auth.AuthenticationDto;
import com.febfes.fftmback.dto.auth.UserDetailsDto;

public interface AuthenticationService {

    AuthenticationDto registerUser(UserDetailsDto userDetailsDto);

    AuthenticationDto authenticateUser(UserDetailsDto userDetailsDto);
}
