package com.febfes.fftmback.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.febfes.fftmback.domain.common.query.FilterRequest;
import com.febfes.fftmback.domain.common.query.FilterSpecification;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.domain.dao.UserView;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.repository.UserViewRepository;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.util.JsonUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, "username", username));
    }

    @Override
    public Long getUserIdByUsername(String username) {
        return userRepository.getIdByUsername(username);
    }

    @Override
    public UserEntity getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, id));
        log.info("Received user {} by id={}", userEntity, id);
        return userEntity;
    }

    @Override
    public UserView getUserViewById(Long id) {
        UserView user = userViewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, id));
        log.info("Received user {} by id={}", user, id);
        return user;
    }

    @Override
    public void updateUser(UserEntity user, Long id) {
        UserEntity userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, id));
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setDisplayName(user.getDisplayName());
        userToUpdate.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userToUpdate);
        log.info("Updated user: {}", userToUpdate);
    }

    @Override
    public List<UserEntity> getUsersByFilter(@NonNull String filter) {
        return userRepository.findAll(makeUsersFilter(filter));
    }

    private FilterSpecification<UserEntity> makeUsersFilter(@NonNull String filter) {
        log.info("Receiving users with a filter: {}", filter);
        List<FilterRequest> filters = JsonUtils.convertStringToObject(filter, new TypeReference<>() {
        });
        return new FilterSpecification<>(filters);
    }
}
