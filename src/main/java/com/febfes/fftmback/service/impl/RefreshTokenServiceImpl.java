package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.config.jwt.JwtService;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.RefreshTokenEntity;
import com.febfes.fftmback.dto.auth.RefreshTokenDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.exception.RefreshTokenExpiredException;
import com.febfes.fftmback.repository.RefreshTokenRepository;
import com.febfes.fftmback.service.RefreshTokenService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.util.DateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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
    private final DateProvider dateProvider;

    @Override
    public RefreshTokenEntity getByToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.class.getSimpleName(), "token", token));
        log.info("Received refresh token {} by token={}", refreshToken, token);
        return refreshToken;
    }

    @Override
    public RefreshTokenEntity createRefreshToken(Long userId) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .createDate(dateProvider.getCurrentDate())
                        .userEntity(userService.getUserById(userId))
                        .expiryDate(dateProvider.getCurrentDatePlusSeconds(jwtRefreshExpirationDateInSeconds))
                        .token(UUID.randomUUID().toString())
                        .build()
        );
        log.info("Saved refresh token: {}", refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity refreshToken) {
        if (refreshToken.getExpiryDate().before(dateProvider.getCurrentDate())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenExpiredException(refreshToken.getToken());
        }
        log.info("Token {} hasn't expired yet", refreshToken.getToken());

        return refreshToken;
    }

    @Override
    public void deleteByUserId(Long userId) {
        if (refreshTokenRepository.existsByUserId(userId)) {
            refreshTokenRepository.deleteByUserId(userId);
        } else {
            throw new EntityNotFoundException(ProjectEntity.class.getSimpleName(), "userId", userId.toString());
        }
    }

    @Override
    public RefreshTokenDto refreshToken(String token) {
        RefreshTokenDto refreshTokenDto = Optional.of(getByToken(token))
                .map(this::verifyExpiration)
                .map(RefreshTokenEntity::getUserEntity)
                .map(userEntity -> {
                    String refreshToken = jwtService.generateToken(userEntity);
                    return new RefreshTokenDto(token, refreshToken);
                })
                .get(); // we are sure that there will be no null here, since we handle the exception in getByToken
        log.info("Received refresh token: {}", refreshTokenDto);
        return refreshTokenDto;
    }
}
