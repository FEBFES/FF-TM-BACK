package com.fftmback.authentication.service;


import com.fftmback.authentication.domain.RefreshTokenEntity;
import com.fftmback.authentication.dto.TokenDto;

public interface RefreshTokenService {

    RefreshTokenEntity getByToken(String token);

    RefreshTokenEntity getByUserId(Long userId);

    RefreshTokenEntity updateRefreshToken(RefreshTokenEntity refreshToken);

    RefreshTokenEntity createRefreshToken(Long userId);

    TokenDto refreshToken(String token);

    RefreshTokenEntity getRefreshTokenByUserId(Long userId);
}
