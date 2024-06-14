package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.service.ScheduleService;
import com.febfes.fftmback.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.febfes.fftmback.util.DateUtils.convertLocalDateTimeToDate;

@Service("taskServiceDecorator")
@RequiredArgsConstructor
public class TaskServiceDecorator implements TaskService {

    @Qualifier("taskService")
    private final TaskService taskService;

    private final ScheduleService scheduleService;

    public static final String USER_ID = "userId";
    public static final String TASK_ID = "taskId";
    public static final String DEADLINE = "deadline";
    public static final String JOB = "job";
    public static final String TRIGGER = "trigger";

    @Override
    public List<TaskView> getTasks(int page, int limit, Long columnId, TaskSpec taskSpec) {
        return taskService.getTasks(page, limit, columnId, taskSpec);
    }

    @Override
    public List<TaskView> getTasks(Set<Long> columnId, TaskSpec taskSpec) {
        return taskService.getTasks(columnId, taskSpec);
    }

    @Override
    public TaskView getTaskById(Long id) {
        return taskService.getTaskById(id);
    }

    @Override
    public Long createTask(TaskEntity task, Long userId) {
        Long taskId = taskService.createTask(task, userId);
        if (task.getDeadlineDate() != null) {
            startDeadlineJob(task.getDeadlineDate(), userId, taskId);
        }
        return taskId;
    }

    @Override
    public void updateTask(TaskEntity editTask, Long userId) {
        taskService.updateTask(editTask, userId);
        if (editTask.getDeadlineDate() != null) {
            startDeadlineJob(editTask.getDeadlineDate(), userId, editTask.getId());
        }
    }

    @Override
    public void deleteTask(Long id) {
        taskService.deleteTask(id);
    }

    @SneakyThrows
    private void startDeadlineJob(LocalDateTime deadlineDate, Long userId, Long taskId) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(USER_ID, userId);
        jobDataMap.put(TASK_ID, taskId);
        String jobIdentity = String.format("%s-%s-%s", DEADLINE, JOB, UUID.randomUUID());
        String triggerIdentity = String.format("%s-%s-%s", DEADLINE, TRIGGER, UUID.randomUUID());
        scheduleService.startNewJobAt(convertLocalDateTimeToDate(deadlineDate), jobDataMap, jobIdentity, triggerIdentity);
    }
}
