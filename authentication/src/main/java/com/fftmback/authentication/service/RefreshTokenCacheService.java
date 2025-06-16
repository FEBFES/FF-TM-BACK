package com.fftmback.authentication.service;

import com.fftmback.authentication.dto.RefreshTokenDto;

public interface RefreshTokenCacheService {

    RefreshTokenDto getByToken(String token);

    RefreshTokenDto getByUserId(Long userId);
}
