package com.febfes.fftmback.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.febfes.fftmback.config.jwt.JwtService;
import com.febfes.fftmback.domain.dao.RefreshTokenEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.auth.GetAuthDto;
import com.febfes.fftmback.dto.error.ErrorType;
import com.febfes.fftmback.exception.EntityAlreadyExistsException;
import com.febfes.fftmback.exception.Exceptions;
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

    private static final String USER_STRING = "user";

    private String generateDisplayName() {
        return USER_STRING + generator.generate(6); // generate a 6-character username
    }

    @Override
    public void registerUser(UserEntity user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityAlreadyExistsException(UserEntity.ENTITY_NAME,
                    "email", user.getEmail(), ErrorType.AUTH);
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EntityAlreadyExistsException(UserEntity.ENTITY_NAME,
                    "username", user.getUsername(), ErrorType.AUTH);
        }
        user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
        user.setDisplayName(isNull(user.getDisplayName()) ? generateDisplayName() : user.getDisplayName());
        userRepository.save(user);
        log.info("User with username = {} saved", user.getUsername());
    }

    @Override
    public GetAuthDto authenticateUser(UserEntity user) {
        UserEntity receivedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(Exceptions.userNotFound(user.getUsername()));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        Long userId = receivedUser.getId();

        String jwtToken = jwtService.generateToken(receivedUser);

        RefreshTokenEntity refreshToken = refreshTokenService.getRefreshTokenByUserId(userId);
        return GetAuthDto.builder()
                .accessToken(jwtToken)
                .userId(userId)
                .refreshToken(refreshToken.getToken())
                .build();
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
            log.error("checkAccessTokenExpiration throws JWTDecodeException", e);
            throw e;
        }
    }
}
