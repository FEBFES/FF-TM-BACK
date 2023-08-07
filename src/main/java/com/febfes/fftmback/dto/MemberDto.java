package com.febfes.fftmback.dto;

public record MemberDto(
        Long id,
        String email,
        String username,
        String firstName,
        String lastName,
        String displayName,
        String userPic,
        String roleOnProject
) {
}
