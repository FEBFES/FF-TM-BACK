package com.fftmback.service;


import com.fftmback.domain.NotificationEntity;

import java.util.List;

public interface NotificationService {

    List<NotificationEntity> getNotificationsByUserId(Long userId);

    NotificationEntity getNotificationById(Long id);

    void createNotification(String message, Long userIdTo);

    void changeIsRead(Long notificationId, boolean isRead);

    void deleteNotification(Long id);

    void deleteOutdatedNotifications();
}
