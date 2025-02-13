package com.febfes.fftmback.service;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;

import java.util.Date;

public interface ScheduleService {

    void startNewJobAt(Class<? extends Job> jobClass,
                       Date triggerStartTime,
                       JobDataMap jobDataMap,
                       String jobIdentity,
                       String triggerIdentity) throws SchedulerException;
}
