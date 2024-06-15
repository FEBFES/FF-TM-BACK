package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.schedule.DeleteNotificationJob;
import com.febfes.fftmback.service.ScheduleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final Scheduler scheduler;

    public static final String JOB = "job";
    public static final String TRIGGER = "trigger";
    public static final String DELETE_NOTIFICATION = "delete_notification";

    @PostConstruct
    void postConstruct() throws SchedulerException {
        JobKey jobKey = new JobKey(String.format("%s-%s", DELETE_NOTIFICATION, JOB));
        if (scheduler.checkExists(jobKey)) {
            log.info("{} already exists", jobKey.getName());
            return;
        }
        String triggerIdentity = String.format("%s-%s", DELETE_NOTIFICATION, TRIGGER);
        JobDetail job = JobBuilder.newJob(DeleteNotificationJob.class)
                .withIdentity(jobKey)
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerIdentity)
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 0))
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    @Override
    public void startNewJobAt(
            Class<? extends Job> jobClass,
            Date triggerStartTime,
            JobDataMap jobDataMap,
            String jobIdentity,
            String triggerIdentity
    ) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(jobIdentity)
                .usingJobData(jobDataMap)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerIdentity)
                .startAt(triggerStartTime)
                .build();

        scheduler.scheduleJob(job, trigger);
    }
}
