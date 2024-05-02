package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiGet;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.febfes.fftmback.config.auth.RoleCheckerComponent;
import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.dto.MemberDto;
import com.febfes.fftmback.mapper.UserMapper;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.service.project.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("v1/projects")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "Project member")
public class MemberController {

    private final ProjectMemberService projectMemberService;
    private final UserService userService;
    private final RoleCheckerComponent roleCheckerComponent;
    private final UserMapper userMapper;

    @Operation(summary = "Get project members")
    @ApiGet(path = "{id}/members")
    public List<MemberDto> getProjectMembers(@PathVariable Long id) {
        return userMapper.memberProjectionToMemberDto(userService.getProjectMembersWithRole(id));
    }

    @Operation(summary = "Add new members to the project")
    @PostMapping(path = "{id}/members")
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Only owner can add a new member", content = @Content)
    public List<MemberDto> addNewMembers(@PathVariable Long id, @RequestBody List<Long> memberIds) {
        roleCheckerComponent.checkIfHasRole(id, RoleName.MEMBER_PLUS);
        projectMemberService.addNewMembers(id, memberIds);
        return userMapper.memberProjectionToMemberDto(userService.getProjectMembersWithRole(id, Set.copyOf(memberIds)));
    }

    @Operation(summary = "Delete member from project")
    @DeleteMapping(path = "{id}/members/{memberId}")
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Only owner can remove a member", content = @Content)
    public MemberDto removeMember(@PathVariable Long id, @PathVariable Long memberId) {
        roleCheckerComponent.checkIfUserIsOwner(id, memberId);
        roleCheckerComponent.checkIfHasRole(id, RoleName.MEMBER_PLUS);
        return projectMemberService.removeMember(id, memberId);
    }
}
