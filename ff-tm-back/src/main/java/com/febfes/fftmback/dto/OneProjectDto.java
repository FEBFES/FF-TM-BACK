package com.febfes.fftmback.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OneProjectDto(
        Long id,
        String name,
        String description,
        LocalDateTime createDate,
        Boolean isFavourite,
        List<MemberDto> members,
        RoleDto userRoleOnProject
) {
}
