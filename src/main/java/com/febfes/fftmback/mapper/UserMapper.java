package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.UserEntity;
import com.febfes.fftmback.dto.EditUserDto;
import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.dto.auth.UserDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "encryptedPassword", source = "password")
    UserEntity userDetailsDtoToUser(UserDetailsDto userDetailsDto);

    @Mapping(target = "encryptedPassword", source = "password")
    UserEntity editUserDtoToUser(EditUserDto editUserDto);

    UserDto userToUserDto(UserEntity userEntity);
}
