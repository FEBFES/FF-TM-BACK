package com.febfes.fftmback.schedule;

import com.febfes.fftmback.domain.dao.NotificationEntity;
import com.febfes.fftmback.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
@DisallowConcurrentExecution
public class DeleteNotificationJob implements Job {

    private final NotificationRepository notificationRepository;

    private static final Long NOTIFICATION_LIFE_DAYS = 30L;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        LocalDateTime now = LocalDateTime.now();
        List<NotificationEntity> notifications = notificationRepository.findByIsReadAndCreateDateBefore(
                true, now.minusDays(NOTIFICATION_LIFE_DAYS));
        if (!notifications.isEmpty()) {
            notificationRepository.deleteAll(notifications);
            log.info("Read notifications with a creation date of {} days ago have been deleted", NOTIFICATION_LIFE_DAYS);
        }
    }
}
