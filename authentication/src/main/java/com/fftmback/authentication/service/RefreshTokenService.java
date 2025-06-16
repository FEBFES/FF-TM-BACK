package com.fftmback.authentication.service;


import com.fftmback.authentication.domain.RefreshTokenEntity;
import com.fftmback.authentication.dto.RefreshTokenDto;
import com.fftmback.authentication.dto.TokenDto;

public interface RefreshTokenService {

    RefreshTokenEntity updateRefreshToken(RefreshTokenDto refreshToken);

    RefreshTokenEntity createRefreshToken(Long userId);

    TokenDto refreshToken(String token);

    RefreshTokenEntity getRefreshTokenByUserId(Long userId);
}
