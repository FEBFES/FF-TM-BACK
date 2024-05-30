package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.NotificationEntity;
import com.febfes.fftmback.repository.NotificationRepository;
import com.febfes.fftmback.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationEntity> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdTo(userId);
    }

    @Override
    public List<NotificationEntity> getNotificationsByUserIdAndIsRead(Long userId, Boolean isRead) {
        return notificationRepository.findByUserIdToAndIsRead(userId, isRead);
    }
}
