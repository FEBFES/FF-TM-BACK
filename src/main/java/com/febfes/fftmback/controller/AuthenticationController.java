package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiCreate;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.febfes.fftmback.domain.UserEntity;
import com.febfes.fftmback.dto.auth.RefreshTokenDto;
import com.febfes.fftmback.dto.auth.TokenDto;
import com.febfes.fftmback.dto.auth.UserDetailsDto;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Operation(summary = "Register new user")
    @ApiCreate(path = "register")
    @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    public TokenDto register(@RequestBody @Valid UserDetailsDto userDetailsDto) {
        return authenticationService.registerUser(userDetailsDto);
    }

    @Operation(summary = "User authentication using username and password")
    @ApiCreate(path = "authenticate")
    @ApiResponse(responseCode = "404", description = "User not found by username", content = @Content)
    public RefreshTokenDto authenticate(@RequestBody @Valid UserDetailsDto userDetailsDto) {
        return authenticationService.authenticateUser(userDetailsDto);
    }

    @Operation(summary = "Get refresh token. You need to send an existent Refresh Token")
    @PostMapping("refresh-token")
    @ApiResponse(responseCode = "401", description = "Refresh token you sent has expired", content = @Content)
    @ApiResponse(responseCode = "404", description = "Token not found in db", content = @Content)
    public RefreshTokenDto refreshToken(@RequestBody TokenDto tokenDto) {
        String token = tokenDto.token();
        return refreshTokenService.refreshToken(token);
    }

    @Operation(summary = "Logout user")
    @PostMapping("logout")
    @ProtectedApi
    @ApiResponse(responseCode = "404", description = "Token not found in db by userId", content = @Content)
    public void logoutUser() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        refreshTokenService.deleteByUserId(userId);
    }

    @Operation(summary = "Checking if the token has expired")
    @PostMapping("has-token-expired")
    @ApiResponse(responseCode = "401", description = "Access token you sent has expired", content = @Content)
    public boolean hasTokenExpired(@RequestBody TokenDto tokenDto) {
        return authenticationService.hasTokenExpired(tokenDto.token());
    }
}
