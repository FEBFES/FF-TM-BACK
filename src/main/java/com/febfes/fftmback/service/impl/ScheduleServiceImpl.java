package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.schedule.TaskDeadlineJob;
import com.febfes.fftmback.service.ScheduleService;
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

    @Override
    public void startNewJobAt(
            Date triggerStartTime,
            JobDataMap jobDataMap,
            String jobIdentity,
            String triggerIdentity
    ) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(TaskDeadlineJob.class)
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
