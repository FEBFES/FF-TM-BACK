package com.fftmback.authentication.mapper;

import com.fftmback.authentication.domain.RefreshTokenEntity;
import com.fftmback.authentication.dto.RefreshTokenDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    @Mapping(target = "username", source = "userEntity.username")
    @Mapping(target = "userId", source = "userEntity.id")
    RefreshTokenDto refreshTokenEntityToDto(RefreshTokenEntity refreshTokenEntity);
}
