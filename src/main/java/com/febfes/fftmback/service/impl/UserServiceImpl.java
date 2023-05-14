package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
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
    public void updateUser(
            UserEntity user,
            Long id
    ) {
        UserEntity userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, id));
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setDisplayName(user.getDisplayName());
        userToUpdate.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userToUpdate);
        log.info("Updated user: {}", userToUpdate);
    }
}
