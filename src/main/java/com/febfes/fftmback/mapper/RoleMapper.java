package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.dto.RoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDto roleToRoleDto(RoleEntity role);
}
