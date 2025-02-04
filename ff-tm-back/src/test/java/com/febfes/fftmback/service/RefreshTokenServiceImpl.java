package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.RefreshTokenEntity;
import com.febfes.fftmback.dto.auth.TokenDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.repository.RefreshTokenRepository;
import com.febfes.fftmback.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

import static com.febfes.fftmback.util.DateUtils.getCurrentLocalDateTimePlusDuration;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refreshExpirationDateDuration}")
    private Duration jwtRefreshExpirationDateDuration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JwtTestService jwtTestService;

    @Override
    public RefreshTokenEntity getByToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(Exceptions.refreshTokenNotFound(token));
        log.info("Received refresh token entity with id={} by token={}", refreshToken.getId(), token);
        return refreshToken;
    }

    @Override
    public RefreshTokenEntity getByUserId(Long userId) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(Exceptions.refreshTokenNotFoundByUserId(userId));
        log.info("Received refresh token entity with id={} by user_id={}", refreshToken.getId(), userId);
        return refreshToken;
    }

    @Override
    public RefreshTokenEntity updateRefreshToken(RefreshTokenEntity refreshToken) {
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(getCurrentLocalDateTimePlusDuration(jwtRefreshExpirationDateDuration));
        RefreshTokenEntity updatedRefreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Updated refresh token entity: {}", updatedRefreshToken);
        return updatedRefreshToken;
    }

    @Override
    public RefreshTokenEntity createRefreshToken(Long userId) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .userEntity(userService.getUserById(userId))
                        .expiryDate(getCurrentLocalDateTimePlusDuration(jwtRefreshExpirationDateDuration))
                        .token(UUID.randomUUID().toString())
                        .build()
        );
        log.info("Saved refresh token entity with id={}", refreshToken.getId());
        return refreshToken;
    }

    @Override
    public TokenDto refreshToken(String token) {
        RefreshTokenEntity refreshTokenEntity = getByToken(token);
        String accessToken = jwtTestService.generateToken(refreshTokenEntity.getUserEntity());
        RefreshTokenEntity updatedRefreshTokenEntity = updateRefreshToken(refreshTokenEntity);
        log.info("Refresh token entity with id={} was refreshed", updatedRefreshTokenEntity.getId());
        return new TokenDto(accessToken, updatedRefreshTokenEntity.getToken());
    }

    @Override
    public RefreshTokenEntity getRefreshTokenByUserId(Long userId) {
        try {
            RefreshTokenEntity existedRefreshToken = getByUserId(userId);
            if (DateUtils.isDateBeforeCurrentDate(existedRefreshToken.getExpiryDate())) {
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
