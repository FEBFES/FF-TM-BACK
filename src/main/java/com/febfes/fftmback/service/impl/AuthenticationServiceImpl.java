package com.febfes.fftmback.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.febfes.fftmback.config.jwt.JwtService;
import com.febfes.fftmback.domain.common.Role;
import com.febfes.fftmback.domain.dao.RefreshTokenEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.auth.TokenDto;
import com.febfes.fftmback.exception.EntityAlreadyExistsException;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.exception.TokenExpiredException;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.RefreshTokenService;
import com.febfes.fftmback.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void registerUser(UserEntity user) {
        if (userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())) {
            throw new EntityAlreadyExistsException(UserEntity.class.getSimpleName());
        }
        user.setCreateDate(DateUtils.getCurrentDate());
        user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.MEMBER);
        userRepository.save(user);
        log.info("User saved: {}", user);
    }

    @Override
    public TokenDto authenticateUser(UserEntity user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        UserEntity receivedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getSimpleName(),
                        "username", user.getUsername()));

        String jwtToken = jwtService.generateToken(receivedUser);

        try {
            RefreshTokenEntity existedRefreshToken = refreshTokenService.getByUserId(receivedUser.getId());
            if (DateUtils.isDateBeforeCurrentDate(existedRefreshToken.getExpiryDate())) {
                RefreshTokenEntity updatedRefreshToken = refreshTokenService.updateRefreshToken(existedRefreshToken);
                log.info("User with id={} authenticated with updated refresh token", user.getId());
                return new TokenDto(jwtToken, updatedRefreshToken.getToken());
            }
            log.info("User with id={} authenticated with existed non expired refresh token", user.getId());
            return new TokenDto(jwtToken, existedRefreshToken.getToken());
        } catch (EntityNotFoundException ignored) {
            log.info("There is no refresh token in db for user with id={}", receivedUser.getId());
        }

        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(receivedUser.getId());
        log.info("User with id={} authenticated", user.getId());
        return new TokenDto(jwtToken, refreshToken.getToken());
    }

    @Override
    public void checkAccessTokenExpiration(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            Date expiresAt = decodedJWT.getExpiresAt();
            if (DateUtils.isDateBeforeCurrentDate(expiresAt)) {
                throw new TokenExpiredException(token);
            }
        } catch (JWTDecodeException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
