package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiGet;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.febfes.fftmback.dto.MemberDto;
import com.febfes.fftmback.service.project.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("v1/projects")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "Project member")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @Operation(summary = "Get project members")
    @ApiGet(path = "{id}/members")
    public List<MemberDto> getProjectMembers(@PathVariable Long id) {
        return projectMemberService.getProjectMembersWithRole(id);
    }

    @Operation(summary = "Add new members to the project")
    @PostMapping(path = "{id}/members")
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Only owner can add a new member", content = @Content)
    @PreAuthorize("hasAuthority(T(com.febfes.fftmback.domain.RoleName).MEMBER_PLUS.name())")
    public List<MemberDto> addNewMembers(@PathVariable Long id, @RequestBody List<Long> memberIds) {
        projectMemberService.addNewMembers(id, memberIds);
        return projectMemberService.getProjectMembersWithRole(id, Set.copyOf(memberIds));
    }

    @Operation(summary = "Delete member from project")
    @DeleteMapping(path = "{id}/members/{memberId}")
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Only owner can remove a member", content = @Content)
    @PreAuthorize("hasAuthority(T(com.febfes.fftmback.domain.RoleName).OWNER.name())")
    public MemberDto removeMember(@PathVariable Long id, @PathVariable Long memberId) {
        return projectMemberService.removeMember(id, memberId);
    }
}
