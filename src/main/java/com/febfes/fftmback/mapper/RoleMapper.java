package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.dto.RoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

// TODO: delete? it's isn't used anywhere
@Mapper
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDto roleProjectionToRoleDto(RoleEntity role);
}
