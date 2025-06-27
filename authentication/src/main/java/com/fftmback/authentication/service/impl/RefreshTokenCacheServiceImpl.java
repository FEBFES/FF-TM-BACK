package com.fftmback.authentication.service.impl;

import com.fftmback.authentication.config.RedisConfig;
import com.fftmback.authentication.domain.RefreshTokenEntity;
import com.fftmback.authentication.dto.RefreshTokenDto;
import com.fftmback.authentication.exception.EntityNotFoundException;
import com.fftmback.authentication.mapper.RefreshTokenMapper;
import com.fftmback.authentication.repository.RefreshTokenRepository;
import com.fftmback.authentication.service.RefreshTokenCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenCacheServiceImpl implements RefreshTokenCacheService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    @Cacheable(value = RedisConfig.REFRESH_TOKENS_CACHE_NAME, key = "#token")
    public RefreshTokenDto getByToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException(RefreshTokenEntity.ENTITY_NAME, "token", token));
        log.info("Received refresh token entity with id={} by token={}", refreshToken.getId(), token);
        return refreshTokenMapper.refreshTokenEntityToDto(refreshToken);
    }

    @Override
    @Cacheable(value = RedisConfig.REFRESH_TOKENS_BY_USER_CACHE_NAME, key = "#userId")
    public RefreshTokenDto getByUserId(Long userId) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(RefreshTokenEntity.ENTITY_NAME, "userId", userId.toString()));
        log.info("Received refresh token entity with id={} by user_id={}", refreshToken.getId(), userId);
        return refreshTokenMapper.refreshTokenEntityToDto(refreshToken);
    }
}
