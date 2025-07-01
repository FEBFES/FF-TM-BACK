package com.fftmback.authentication.service.impl;

import com.febfes.fftmback.dto.ErrorType;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.domain.UserView;
import com.fftmback.authentication.domain.spec.UserSpec;
import com.fftmback.authentication.exception.Exceptions;
import com.fftmback.authentication.repository.UserRepository;
import com.fftmback.authentication.repository.UserViewRepository;
import com.fftmback.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, "username", username, ErrorType.AUTH));
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public UserEntity getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, id));
        log.info("Received user by id={}", id);
        return userEntity;
    }

    @Override
    public UserView getUserViewById(Long id) {
        UserView user = userViewRepository.findById(id).orElseThrow(Exceptions.userNotFoundById(id));
        log.info("Received user by id={}", id);
        return user;
    }

    @Override
    public void updateUser(UserEntity user, Long id) {
        UserEntity userToUpdate = userRepository.findById(id).orElseThrow(Exceptions.userNotFoundById(id));
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setDisplayName(user.getDisplayName());
        // password can't be null, but front can send null password as it doesn't have it
        if (nonNull(user.getPassword())) {
            userToUpdate.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(userToUpdate);
        log.info("Updated user with id={}", id);
    }

    @Override
    public List<UserView> getUsersByFilter(UserSpec userSpec) {
        return userViewRepository.findAll(userSpec);
    }
}
