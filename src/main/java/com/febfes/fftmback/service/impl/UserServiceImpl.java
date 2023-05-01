package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.domain.dao.UserPicEntity;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.exception.SaveFileException;
import com.febfes.fftmback.repository.UserPicRepository;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserPicRepository userPicRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${user-pic.folder}")
    private String userPicFolder;

    private static final String USER_PIC_URN = "/users/%d/user-pic";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getSimpleName(),
                        "username", username));
    }

    @Override
    public Long getUserIdByUsername(String username) {
        return userRepository.getIdByUsername(username);
    }

    @Override
    public UserEntity getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getSimpleName(), id));
        log.info("Received user {} by id={}", userEntity, id);
        return userEntity;
    }

    @Override
    public void updateUser(
            UserEntity user,
            Long id
    ) {
        UserEntity userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getSimpleName(), id));
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userToUpdate);
        log.info("Updated user: {}", userToUpdate);
    }

    @Override
    public void saveUserPic(Long userId, MultipartFile pic) {
        UserPicEntity userPic = userPicRepository.findUserPicEntityByUserId(userId)
                .orElseGet(() -> UserPicEntity.builder().userId(userId).build());
        String filePath = "%s%d.jpg".formatted(userPicFolder, userId);
        userPic.setFilePath(filePath);
        userPic.setFileUrn(String.format(USER_PIC_URN, userId));
        try {
            pic.transferTo(new File(filePath));
            userPicRepository.save(userPic);
            log.info("User pic for user with id={} saved", userId);
        } catch (IOException e) {
            throw new SaveFileException(pic.getName());
        }
    }

    @Override
    public UserPicEntity getUserPic(Long userId) {
        return userPicRepository.findUserPicEntityByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserPicEntity.class.getSimpleName(), userId));
    }

    @Override
    public byte[] getUserPicContent(Long userId) throws IOException {
        UserPicEntity userPicEntity = getUserPic(userId);
        String filePath = userPicEntity.getFilePath();
        return Files.readAllBytes(new File(filePath).toPath());
    }
}
