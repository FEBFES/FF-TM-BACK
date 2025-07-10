package com.fftmback.authentication.service.impl;

import com.febfes.fftmback.dto.ErrorType;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.domain.spec.UserSpec;
import com.fftmback.authentication.dto.UserDto;
import com.fftmback.authentication.exception.Exceptions;
import com.fftmback.authentication.mapper.UserMapper;
import com.fftmback.authentication.repository.UserRepository;
import com.fftmback.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, "username", username, ErrorType.AUTH));
    }

    @Override
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, id));
    }

    @Override
    public UserDto getUserDtoById(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(Exceptions.userNotFoundById(id));
        log.info("Received user dto by id={}", id);
        return userMapper.mapToUserDto(user);
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
    public List<UserDto> getUsersByFilter(UserSpec userSpec) {
        return userMapper.mapToUserDto(userRepository.findAll(userSpec));
    }

    @Override
    public List<UserDto> getUsersByIds(Set<Long> ids) {
        return userMapper.mapToUserDto(userRepository.findAllById(ids));
    }
}
