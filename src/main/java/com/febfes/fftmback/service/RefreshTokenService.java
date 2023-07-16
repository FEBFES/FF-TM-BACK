package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.RefreshTokenEntity;
import com.febfes.fftmback.dto.auth.TokenDto;

public interface RefreshTokenService {

    RefreshTokenEntity getByToken(String token);

    RefreshTokenEntity getByUserId(Long userId);

    RefreshTokenEntity updateRefreshToken(RefreshTokenEntity refreshToken);

    RefreshTokenEntity createRefreshToken(Long userId);

    TokenDto refreshToken(String token);

    RefreshTokenEntity getOrCreateRefreshToken(Long userId);
}
