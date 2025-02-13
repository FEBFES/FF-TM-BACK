package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.common.specification.UserSpec;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.domain.dao.UserView;
import com.febfes.fftmback.domain.projection.MemberProjection;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {

    Long getUserIdByUsername(String username);

    UserEntity getUserById(Long id);

    UserView getUserViewById(Long id);

    void updateUser(UserEntity user, Long id);

    List<UserView> getUsersByFilter(UserSpec userSpec);

    List<MemberProjection> getProjectMembersWithRole(Long projectId);

    MemberProjection getProjectMemberWithRole(Long projectId, Long memberId);

    List<MemberProjection> getProjectMembersWithRole(Long projectId, Set<Long> membersIds);
}
