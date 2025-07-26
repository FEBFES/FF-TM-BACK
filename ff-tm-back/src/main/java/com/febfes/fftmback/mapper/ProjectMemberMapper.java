package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.projection.MemberIdRoleProjection;
import com.febfes.fftmback.dto.MemberDto;
import com.febfes.fftmback.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.displayName", target = "displayName")
    @Mapping(source = "user.userPic", target = "userPic")
    @Mapping(source = "projection.roleOnProject", target = "roleOnProject")
    MemberDto mapToMemberDto(MemberIdRoleProjection projection, UserDto user);
}
