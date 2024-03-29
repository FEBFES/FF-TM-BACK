package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.specification.UserSpec;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.domain.dao.UserView;
import com.febfes.fftmback.domain.projection.MemberProjection;
import com.febfes.fftmback.dto.MemberDto;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.mapper.UserMapper;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.repository.UserViewRepository;
import com.febfes.fftmback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserViewRepository userViewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(Exceptions.userNotFound(username));
    }

    @Override
    public Long getUserIdByUsername(String username) {
        return userRepository.getIdByUsername(username);
    }

    @Override
    public UserEntity getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(Exceptions.userNotFoundById(id));
        log.info("Received user {} by id={}", userEntity, id);
        return userEntity;
    }

    @Override
    public UserView getUserViewById(Long id) {
        UserView user = userViewRepository.findById(id)
                .orElseThrow(Exceptions.userNotFoundById(id));
        log.info("Received user {} by id={}", user, id);
        return user;
    }

    @Override
    public void updateUser(UserEntity user, Long id) {
        UserEntity userToUpdate = userRepository.findById(id)
                .orElseThrow(Exceptions.userNotFoundById(id));
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setDisplayName(user.getDisplayName());
        if (nonNull(user.getPassword())) {  // password can't be null, but front can send null password as it doesn't have it
            userToUpdate.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(userToUpdate);
        log.info("Updated user: {}", userToUpdate);
    }

    @Override
    public List<UserView> getUsersByFilter(UserSpec userSpec) {
        return userViewRepository.findAll(userSpec);
    }

    @Override
    public List<MemberDto> getProjectMembersWithRole(Long projectId) {
        return userViewRepository.getProjectMembersWithRole(projectId).stream()
                .map(UserMapper.INSTANCE::memberProjectionToMemberDto)
                .toList();
    }

    @Override
    public MemberDto getProjectMemberWithRole(Long projectId, Long memberId) {
        MemberProjection member = userViewRepository.getProjectMemberWithRole(projectId, memberId)
                .orElseThrow(Exceptions.userNotFoundById(memberId));
        return UserMapper.INSTANCE.memberProjectionToMemberDto(member);
    }

    @Override
    public List<MemberDto> getProjectMembersWithRole(Long projectId, List<Long> membersIds) {
        return membersIds.stream()
                .map(memberId -> getProjectMemberWithRole(projectId, memberId))
                .toList();
    }
}
