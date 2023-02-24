package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.domain.dao.UserPicEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends UserDetailsService {

    Long getUserIdByUsername(String username);

    UserEntity getUserById(Long id);

    void updateUser(UserEntity user, Long id);

    void saveUserPic(Long userId, MultipartFile pic);

    UserPicEntity getUserPic(Long userId);
}
