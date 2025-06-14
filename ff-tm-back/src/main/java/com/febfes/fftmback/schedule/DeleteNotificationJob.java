package com.febfes.fftmback.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
@DisallowConcurrentExecution
public class DeleteNotificationJob implements Job {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String DELETE_NOTIFICATIONS_TOPIC = "notification-delete-topic";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        kafkaTemplate.send(DELETE_NOTIFICATIONS_TOPIC, null);
    }
}
