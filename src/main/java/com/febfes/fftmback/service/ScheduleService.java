package com.febfes.fftmback.service;

import org.quartz.JobDataMap;
import org.quartz.SchedulerException;

import java.util.Date;

public interface ScheduleService {

    void startNewJobAt(Date triggerStartTime,
                       JobDataMap jobDataMap,
                       String jobIdentity,
                       String triggerIdentity) throws SchedulerException;
}
