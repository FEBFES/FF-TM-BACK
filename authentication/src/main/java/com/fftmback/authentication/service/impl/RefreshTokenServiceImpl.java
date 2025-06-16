package com.fftmback.authentication.service.impl;

import com.fftmback.authentication.config.jwt.JwtService;
import com.fftmback.authentication.domain.RefreshTokenEntity;
import com.fftmback.authentication.dto.RefreshTokenDto;
import com.fftmback.authentication.dto.TokenDto;
import com.fftmback.authentication.exception.EntityNotFoundException;
import com.fftmback.authentication.repository.RefreshTokenRepository;
import com.fftmback.authentication.service.RefreshTokenService;
import com.fftmback.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
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
    private final RefreshTokenCacheServiceImpl refreshTokenCacheService;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "refreshTokens", key = "#dto.token", beforeInvocation = true),
            @CacheEvict(value = "refreshTokensByUser", key = "#dto.userId", beforeInvocation = true)
    })
    public RefreshTokenEntity updateRefreshToken(RefreshTokenDto dto) {
        val refreshToken = refreshTokenRepository.findByUserId(dto.userId());
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
        RefreshTokenDto refreshTokenEntity = refreshTokenCacheService.getByToken(token);
        String accessToken = jwtService.generateToken(refreshTokenEntity.getUserEntity());
        RefreshTokenEntity updatedRefreshTokenEntity = updateRefreshToken(refreshTokenEntity);
        log.info("Refresh token entity with id={} was refreshed", updatedRefreshTokenEntity.getId());
        return new TokenDto(accessToken, updatedRefreshTokenEntity.getToken());
    }

    @Override
    public RefreshTokenEntity getRefreshTokenByUserId(Long userId) {
        try {
            RefreshTokenDto existedRefreshToken = refreshTokenCacheService.getByUserId(userId);
            if (existedRefreshToken.expiryDate().isBefore(LocalDateTime.now())) {
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
