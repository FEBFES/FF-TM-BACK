package com.fftmback.listener;

import com.fftmback.dto.SendNotificationDto;
import com.fftmback.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    void createListener(SendNotificationDto data) {
        notificationService.createNotification(data.message(), data.userId());
    }

    @KafkaListener(topics = "notification-delete-topic", groupId = "notification-delete-group")
    void deleteListener() {
        notificationService.deleteOutdatedNotifications();
    }
}
