package com.febfes.fftmback.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.febfes.fftmback.config.jwt.JwtService;
import com.febfes.fftmback.domain.RefreshTokenEntity;
import com.febfes.fftmback.domain.Role;
import com.febfes.fftmback.domain.UserEntity;
import com.febfes.fftmback.dto.auth.RefreshTokenDto;
import com.febfes.fftmback.dto.auth.TokenDto;
import com.febfes.fftmback.exception.EntityAlreadyExistsException;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.RefreshTokenService;
import com.febfes.fftmback.util.DateProvider;
import io.jsonwebtoken.ExpiredJwtException;
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
    private final DateProvider dateProvider;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenDto registerUser(UserEntity user) {
        if (userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())) {
            throw new EntityAlreadyExistsException(UserEntity.class.getSimpleName());
        }
        user.setCreateDate(dateProvider.getCurrentDate());
        user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.MEMBER);
        userRepository.save(user);
        log.info("User saved: {}", user);
        String jwtToken = jwtService.generateToken(user);
        return new TokenDto(jwtToken);
    }

    @Override
    public RefreshTokenDto authenticateUser(UserEntity user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        UserEntity recivedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getSimpleName(),
                        "username", user.getUsername()));

        String jwtToken = jwtService.generateToken(recivedUser);

        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(recivedUser.getId());
        log.info("User authenticated");
        return new RefreshTokenDto(jwtToken, refreshToken.getToken());
    }

    @Override
    public boolean hasTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            Date expiresAt = decodedJWT.getExpiresAt();
            return expiresAt.before(dateProvider.getCurrentDate());
        } catch (JWTDecodeException e) {
            log.error(e.getMessage());
            throw new ExpiredJwtException(null, null, e.getMessage(), e.getCause());
        }
    }
}
