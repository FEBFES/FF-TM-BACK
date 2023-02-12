package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.Role;
import com.febfes.fftmback.domain.UserEntity;
import com.febfes.fftmback.dto.auth.UserDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "createDate", source = "createDate")
    @Mapping(target = "encryptedPassword", source = "encryptedPassword")
    @Mapping(target = "role", source = "role")
    UserEntity userDetailsDtoToUser(
            UserDetailsDto userDetailsDto,
            Date createDate,
            String encryptedPassword,
            Role role
    );
}
