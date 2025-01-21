package com.fftmback.listener;

import com.fftmback.dto.SendNotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationListener {

    @KafkaListener(topics = "notification-topic", groupId = "group1")
    void listener(SendNotificationDto data) {
        log.info("Received message [{}] in group1", data);
    }
}
