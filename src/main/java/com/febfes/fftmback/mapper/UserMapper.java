package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.domain.dao.UserView;
import com.febfes.fftmback.domain.projection.MemberProjection;
import com.febfes.fftmback.dto.EditUserDto;
import com.febfes.fftmback.dto.MemberDto;
import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.dto.auth.AuthenticationDto;
import com.febfes.fftmback.dto.auth.UserDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static java.util.Objects.isNull;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "encryptedPassword", source = "password")
    UserEntity userDetailsDtoToUser(UserDetailsDto userDetailsDto);

    @Mapping(target = "encryptedPassword", source = "password")
    UserEntity authenticationDtoToUser(AuthenticationDto authenticationDto);

    @Mapping(target = "encryptedPassword", source = "password")
    UserEntity editUserDtoToUser(EditUserDto editUserDto);

    @Mapping(target = "userPic", qualifiedByName = "userPicToString")
    UserDto userViewToUserDto(UserView userView);

    MemberDto memberProjectionToMemberDto(MemberProjection memberProjection);

    @Named("userPicToString")
    static String userPicToString(FileEntity userPic) {
        return isNull(userPic) ? null : userPic.getFileUrn();
    }
}
