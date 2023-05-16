package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.config.jwt.JwtService;
import com.febfes.fftmback.domain.dao.RefreshTokenEntity;
import com.febfes.fftmback.dto.auth.TokenDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.RefreshTokenRepository;
import com.febfes.fftmback.service.RefreshTokenService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refreshExpirationDateInSeconds}")
    private int jwtRefreshExpirationDateInSeconds;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public RefreshTokenEntity getByToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException(RefreshTokenEntity.ENTITY_NAME, "token", token));
        log.info("Received refresh token {} by token={}", refreshToken, token);
        return refreshToken;
    }

    @Override
    public RefreshTokenEntity getByUserId(Long userId) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(RefreshTokenEntity.ENTITY_NAME, "userId", userId.toString()));
        log.info("Received refresh token {} by user id={}", refreshToken, userId);
        return refreshToken;
    }

    @Override
    public RefreshTokenEntity updateRefreshToken(RefreshTokenEntity refreshToken) {
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(DateUtils.getCurrentDatePlusSeconds(jwtRefreshExpirationDateInSeconds));
        RefreshTokenEntity updatedRefreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Updated refresh token: {}", updatedRefreshToken);
        return updatedRefreshToken;
    }

    @Override
    public RefreshTokenEntity createRefreshToken(Long userId) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .userEntity(userService.getUserById(userId))
                        .expiryDate(DateUtils.getCurrentDatePlusSeconds(jwtRefreshExpirationDateInSeconds))
                        .token(UUID.randomUUID().toString())
                        .build()
        );
        log.info("Saved refresh token: {}", refreshToken);
        return refreshToken;
    }

    @Override
    public TokenDto refreshToken(String token) {
        RefreshTokenEntity refreshTokenEntity = getByToken(token);
        String accessToken = jwtService.generateToken(refreshTokenEntity.getUserEntity());
        RefreshTokenEntity updatedRefreshTokenEntity = updateRefreshToken(refreshTokenEntity);
        log.info("Refresh token with id={} refreshed", updatedRefreshTokenEntity.getId());
        return new TokenDto(accessToken, updatedRefreshTokenEntity.getToken());
    }
}
