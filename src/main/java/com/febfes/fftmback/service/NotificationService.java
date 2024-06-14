package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.NotificationEntity;

import java.util.List;

public interface NotificationService {

    List<NotificationEntity> getNotificationsByUserId(Long userId);

    List<NotificationEntity> getNotificationsByUserIdAndIsRead(Long userId, Boolean isRead);

    void createNotification(String message, Long userIdTo);
}
