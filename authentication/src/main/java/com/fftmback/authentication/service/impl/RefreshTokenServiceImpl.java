package com.fftmback.authentication.service.impl;

import com.fftmback.authentication.config.jwt.JwtService;
import com.fftmback.authentication.domain.RefreshTokenEntity;
import com.fftmback.authentication.dto.TokenDto;
import com.fftmback.authentication.exception.EntityNotFoundException;
import com.fftmback.authentication.repository.RefreshTokenRepository;
import com.fftmback.authentication.service.RefreshTokenService;
import com.fftmback.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refreshExpirationDateDuration}")
    private Duration jwtRefreshExpirationDateDuration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public RefreshTokenEntity getByToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException(RefreshTokenEntity.ENTITY_NAME, "token", token));
        log.info("Received refresh token entity with id={} by token={}", refreshToken.getId(), token);
        return refreshToken;
    }

    @Override
    public RefreshTokenEntity getByUserId(Long userId) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(RefreshTokenEntity.ENTITY_NAME, "userId", userId.toString()));
        log.info("Received refresh token entity with id={} by user_id={}", refreshToken.getId(), userId);
        return refreshToken;
    }

    @Override
    public RefreshTokenEntity updateRefreshToken(RefreshTokenEntity refreshToken) {
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plus(jwtRefreshExpirationDateDuration));
        RefreshTokenEntity updatedRefreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Updated refresh token entity: {}", updatedRefreshToken);
        return updatedRefreshToken;
    }

    @Override
    public RefreshTokenEntity createRefreshToken(Long userId) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .userEntity(userService.getUserById(userId))
                        .expiryDate(LocalDateTime.now().plus(jwtRefreshExpirationDateDuration))
                        .token(UUID.randomUUID().toString())
                        .build()
        );
        log.info("Saved refresh token entity with id={}", refreshToken.getId());
        return refreshToken;
    }

    @Override
    public TokenDto refreshToken(String token) {
        RefreshTokenEntity refreshTokenEntity = getByToken(token);
        String accessToken = jwtService.generateToken(refreshTokenEntity.getUserEntity());
        RefreshTokenEntity updatedRefreshTokenEntity = updateRefreshToken(refreshTokenEntity);
        log.info("Refresh token entity with id={} was refreshed", updatedRefreshTokenEntity.getId());
        return new TokenDto(accessToken, updatedRefreshTokenEntity.getToken());
    }

    @Override
    public RefreshTokenEntity getRefreshTokenByUserId(Long userId) {
        try {
            RefreshTokenEntity existedRefreshToken = getByUserId(userId);
            if (existedRefreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                RefreshTokenEntity updatedRefreshToken = updateRefreshToken(existedRefreshToken);
                log.info("User with id={} authenticated with updated refresh token", userId);
                return updatedRefreshToken;
            }
            log.info("User with id={} authenticated with existed non expired refresh token", userId);
            return existedRefreshToken;
        } catch (EntityNotFoundException ignored) {
            log.info("There is no refresh token in DB for user with id={}", userId);
        }

        return createRefreshToken(userId);
    }
}
