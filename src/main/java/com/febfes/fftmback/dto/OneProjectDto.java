package com.febfes.fftmback.dto;

import java.util.Date;
import java.util.List;

public record OneProjectDto(
        Long id,
        String name,
        String description,
        Date createDate,
        Boolean isFavourite,
        List<MemberDto> members,
        RoleDto userRoleOnProject
) {
}
