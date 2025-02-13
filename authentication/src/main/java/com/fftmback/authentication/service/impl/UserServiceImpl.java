package com.fftmback.authentication.service.impl;

import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.dto.error.ErrorType;
import com.fftmback.authentication.exception.EntityNotFoundException;
import com.fftmback.authentication.repository.UserRepository;
import com.fftmback.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, "username", username, ErrorType.AUTH));
    }

    @Override
    public UserEntity getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, id));
        log.info("Received user by id={}", id);
        return userEntity;
    }
}
