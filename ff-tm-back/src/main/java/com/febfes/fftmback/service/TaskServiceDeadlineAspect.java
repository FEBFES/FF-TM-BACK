package com.febfes.fftmback.service;

import com.febfes.fftmback.config.jwt.User;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.schedule.TaskDeadlineJob;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.quartz.JobDataMap;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.febfes.fftmback.util.DateUtils.convertLocalDateTimeToDate;

@Aspect
@Service
@RequiredArgsConstructor
public class TaskServiceDeadlineAspect {

    private final ScheduleService scheduleService;

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    public static final String TASK_ID = "taskId";
    public static final String DEADLINE = "deadline";
    public static final String JOB = "job";
    public static final String TRIGGER = "trigger";

    @AfterReturning(value = "execution(* com.febfes.fftmback.service.TaskService.createTask(*, *)) && args(task, user)",
            argNames = "task,user")
    public void afterCreateTask(TaskEntity task, User user) {
        startDeadlineJobIfNeeded(task.getDeadlineDate(), user, task.getId());
    }

    @AfterReturning(value = "execution(* com.febfes.fftmback.service.TaskService.updateTask(*, *)) && args(editTask, user)",
            argNames = "editTask,user")
    public void afterUpdateTask(TaskEntity editTask, User user) {
        startDeadlineJobIfNeeded(editTask.getDeadlineDate(), user, editTask.getId());
    }

    @SneakyThrows
    private void startDeadlineJobIfNeeded(LocalDateTime deadlineDate, User user, Long taskId) {
        if (deadlineDate == null) {
            return;
        }
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(USER_ID, user.id());
        jobDataMap.put(USERNAME, user.username());
        jobDataMap.put(TASK_ID, taskId);
        String jobIdentity = String.format("%s-%s-%s", DEADLINE, JOB, UUID.randomUUID());
        String triggerIdentity = String.format("%s-%s-%s", DEADLINE, TRIGGER, UUID.randomUUID());
        scheduleService.startNewJobAt(TaskDeadlineJob.class, convertLocalDateTimeToDate(deadlineDate),
                jobDataMap, jobIdentity, triggerIdentity);
    }
}
