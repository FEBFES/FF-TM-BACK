package com.fftmback.authentication.mapper;

import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.dto.AuthenticationDto;
import com.fftmback.authentication.dto.UserDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "encryptedPassword", source = "password")
    UserEntity userDetailsDtoToUser(UserDetailsDto userDetailsDto);

    @Mapping(target = "encryptedPassword", source = "password")
    UserEntity authenticationDtoToUser(AuthenticationDto authenticationDto);
}
