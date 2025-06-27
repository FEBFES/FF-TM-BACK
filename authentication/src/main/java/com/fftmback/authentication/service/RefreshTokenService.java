package com.fftmback.authentication.service;


import com.fftmback.authentication.domain.RefreshTokenEntity;
import com.fftmback.authentication.dto.RefreshTokenDto;
import com.fftmback.authentication.dto.TokenDto;

public interface RefreshTokenService {

    String updateRefreshToken(RefreshTokenDto refreshToken);

    RefreshTokenEntity createRefreshToken(Long userId);

    TokenDto refreshToken(String token);

    String getRefreshTokenByUserId(Long userId);
}
