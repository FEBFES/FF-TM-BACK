package com.fftmback.service.impl;

import com.fftmback.domain.NotificationEntity;
import com.fftmback.exception.EntityNotFoundException;
import com.fftmback.repository.NotificationRepository;
import com.fftmback.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationEntity> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdTo(userId).stream()
                .sorted(Comparator.comparing(NotificationEntity::getCreateDate).reversed())
                .toList();
    }

    @Override
    public NotificationEntity getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NotificationEntity.ENTITY_NAME, id));
    }

    @Override
    public void createNotification(String message, Long userIdTo) {
        NotificationEntity notification = NotificationEntity.builder()
                .message(message)
                .userIdTo(userIdTo)
                .build();
        notificationRepository.save(notification);
        log.info("Created notification with ID = {}", notification.getId());
    }

    @Override
    public void changeIsRead(Long notificationId, boolean isRead) {
        NotificationEntity notification = getNotificationById(notificationId);
        notification.setRead(isRead);
        notificationRepository.save(notification);
        log.info("Change isRead to {} for notification with ID = {}", isRead, notification.getId());
    }

    @Override
    public void deleteNotification(Long id) {
        NotificationEntity notification = getNotificationById(id);
        notificationRepository.deleteById(notification.getId());
        log.info("Deleted notification with ID = {}", id);
    }
}
