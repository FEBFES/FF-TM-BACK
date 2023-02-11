package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiCreate;
import com.febfes.fftmback.dto.auth.AuthenticationDto;
import com.febfes.fftmback.dto.auth.UserDetailsDto;
import com.febfes.fftmback.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Register new user")
    @ApiCreate(path = "register")
    @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    public AuthenticationDto register(@RequestBody @Valid UserDetailsDto userDetailsDto) {
        return authenticationService.registerUser(userDetailsDto);
    }

    @Operation(summary = "User authentication using username and password")
    @ApiCreate(path = "authenticate")
    @ApiResponse(responseCode = "404", description = "User not found by username", content = @Content)
    public AuthenticationDto authenticate(@RequestBody @Valid UserDetailsDto userDetailsDto) {
        return authenticationService.authenticateUser(userDetailsDto);
    }
}
