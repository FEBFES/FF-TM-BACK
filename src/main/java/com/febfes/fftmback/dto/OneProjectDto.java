package com.febfes.fftmback.dto;

import java.util.Date;
import java.util.Set;

public record OneProjectDto(
        Long id,
        String name,
        String description,
        Date createDate,
        Boolean isFavourite,
        Set<MemberDto> members,
        RoleDto userRoleOnProject
) {
}
