package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.common.specification.UserSpec;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.domain.dao.UserView;
import com.febfes.fftmback.dto.MemberDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    Long getUserIdByUsername(String username);

    UserEntity getUserById(Long id);

    UserView getUserViewById(Long id);

    void updateUser(UserEntity user, Long id);

    List<UserView> getUsersByFilter(UserSpec userSpec);

    List<MemberDto> getProjectMembersWithRole(Long projectId);

    MemberDto getProjectMemberWithRole(Long projectId, Long memberId);

    List<MemberDto> getProjectMembersWithRole(Long projectId, List<Long> membersIds);
}
