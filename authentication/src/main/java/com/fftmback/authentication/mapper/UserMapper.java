package com.fftmback.authentication.mapper;

import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.dto.AuthenticationDto;
import com.fftmback.authentication.dto.EditUserDto;
import com.fftmback.authentication.dto.UserDetailsDto;
import com.fftmback.authentication.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = com.febfes.fftmback.util.FileUrnUtils.class)
public interface UserMapper {

    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "displayName", ignore = true)
    @Mapping(target = "encryptedPassword", source = "password")
    UserEntity userDetailsDtoToUser(UserDetailsDto userDetailsDto);

    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "displayName", ignore = true)
    @Mapping(target = "encryptedPassword", source = "password")
    UserEntity authenticationDtoToUser(AuthenticationDto authenticationDto);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "encryptedPassword", source = "password")
    UserEntity editUserDtoToUser(EditUserDto editUserDto);

    @Mapping(target = "userPic", expression = "java(com.febfes.fftmback.util.FileUrnUtils.getUserPicUrn(user.getId()))")
    UserDto mapToUserDto(UserEntity user);

    List<UserDto> mapToUserDto(List<UserEntity> user);
}
