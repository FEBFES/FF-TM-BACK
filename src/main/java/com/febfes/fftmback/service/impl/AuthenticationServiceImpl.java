package com.febfes.fftmback.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.febfes.fftmback.config.jwt.JwtService;
import com.febfes.fftmback.domain.common.Role;
import com.febfes.fftmback.domain.dao.RefreshTokenEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.auth.GetAuthDto;
import com.febfes.fftmback.exception.EntityAlreadyExistsException;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.exception.TokenExpiredException;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.RefreshTokenService;
import com.febfes.fftmback.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    private final RandomStringGenerator generator = new RandomStringGenerator
            .Builder()
            .selectFrom('0', '9')
            .build();

    @Override
    public void registerUser(UserEntity user) {
        if (userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())) {
            throw new EntityAlreadyExistsException(UserEntity.ENTITY_NAME);
        }
        user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.MEMBER);
        user.setDisplayName(isNull(user.getDisplayName()) ? generateDisplayUserName() : user.getDisplayName());
        userRepository.save(user);
        log.info("User saved: {}", user);
    }

    private String generateDisplayUserName() {
        return "user" + generator.generate(6); // generate a 6-character username
    }

    @Override
    public GetAuthDto authenticateUser(UserEntity user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        UserEntity receivedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, "username", user.getUsername()));
        Long userId = receivedUser.getId();

        String jwtToken = jwtService.generateToken(receivedUser);

        GetAuthDto.GetAuthDtoBuilder getAuthDto = GetAuthDto.builder()
                .accessToken(jwtToken)
                .userId(userId);
        try {
            RefreshTokenEntity existedRefreshToken = refreshTokenService.getByUserId(userId);
            if (DateUtils.isDateBeforeCurrentDate(existedRefreshToken.getExpiryDate())) {
                RefreshTokenEntity updatedRefreshToken = refreshTokenService.updateRefreshToken(existedRefreshToken);
                log.info("User with id={} authenticated with updated refresh token", userId);
                return getAuthDto.refreshToken(updatedRefreshToken.getToken()).build();
            }
            log.info("User with id={} authenticated with existed non expired refresh token", userId);
            return getAuthDto.refreshToken(existedRefreshToken.getToken()).build();
        } catch (EntityNotFoundException ignored) {
            log.info("There is no refresh token in db for user with id={}", userId);
        }

        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(userId);
        log.info("User with id={} authenticated", userId);
        return getAuthDto.refreshToken(refreshToken.getToken()).build();
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
