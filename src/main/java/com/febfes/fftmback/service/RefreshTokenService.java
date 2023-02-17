package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.RefreshTokenEntity;
import com.febfes.fftmback.dto.auth.RefreshTokenDto;

public interface RefreshTokenService {

    RefreshTokenEntity getByToken(String token);

    RefreshTokenEntity createRefreshToken(Long userId);

    RefreshTokenEntity verifyExpiration(RefreshTokenEntity refreshToken);

    void deleteByUserId(Long userId);

    RefreshTokenDto refreshToken(String token);
}
