package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.projection.MemberProjection;
import com.febfes.fftmback.dto.MemberDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    MemberDto memberProjectionToMemberDto(MemberProjection memberProjection);

    List<MemberDto> memberProjectionToMemberDto(List<MemberProjection> memberProjections);
}
