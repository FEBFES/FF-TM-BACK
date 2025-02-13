package com.fftmback.authentication.controller;

import com.fftmback.authentication.annotation.ApiCreate;
import com.fftmback.authentication.annotation.ApiGet;
import com.fftmback.authentication.dto.*;
import com.fftmback.authentication.mapper.UserMapper;
import com.fftmback.authentication.service.AuthenticationService;
import com.fftmback.authentication.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    @Operation(summary = "Register new user")
    @ApiCreate(path = "register")
    @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    public void register(@RequestBody @Valid UserDetailsDto userDetailsDto) {
        authenticationService.registerUser(userMapper.userDetailsDtoToUser(userDetailsDto));
    }

    @Operation(summary = "User authentication using username and password")
    @ApiCreate(path = "authenticate")
    @ApiResponse(responseCode = "404", description = "User not found by username", content = @Content)
    public GetAuthDto authenticate(@RequestBody @Valid AuthenticationDto authenticationDto) {
        return authenticationService.authenticateUser(userMapper.authenticationDtoToUser(authenticationDto));
    }

    @Operation(summary = "Update refresh and access token. You need to send an existent Refresh Token")
    @PostMapping("refresh-token")
    @ApiResponse(responseCode = "404", description = "Token not found in db", content = @Content)
    public TokenDto refreshToken(@RequestBody RefreshTokenDto tokenDto) {
        String token = tokenDto.refreshToken();
        return refreshTokenService.refreshToken(token);
    }

    @Operation(summary = "Checking if the token has expired")
    @PostMapping("check-token-expiration")
    @ApiResponse(responseCode = "401", description = "Access token you sent has expired", content = @Content)
    public void checkAccessTokenExpiration(@RequestBody AccessTokenDto accessTokenDto) {
        authenticationService.checkAccessTokenExpiration(accessTokenDto.accessToken());
    }

    @Operation(summary = "Validate JWT token")
    @ApiGet("validate-token")
    public ConnValidationResponse validateToken(
            HttpServletRequest request
    ) {
        return authenticationService.validateToken(request);
    }
}
