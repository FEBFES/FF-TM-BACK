package com.febfes.fftmback.schedule;

import com.febfes.fftmback.controller.SseNotificationController;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.SendNotificationDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.project.ProjectManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.febfes.fftmback.service.TaskServiceDeadlineAspect.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
@DisallowConcurrentExecution
public class TaskDeadlineJob implements Job {

    private final SseNotificationController sseNotificationController;

    @Qualifier("projectManagementServiceDecorator")
    private final ProjectManagementService projectManagementService;

    private final TaskService taskService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Long userId = jobDataMap.getLong(USER_ID);
        String username = jobDataMap.getString(USERNAME);
        Long taskId = jobDataMap.getLong(TASK_ID);
        log.info("Executing TaskDeadlineJob: USER_ID: {}", userId);
        try {
            TaskView taskView = taskService.getTaskById(taskId);
            ProjectEntity project = projectManagementService.getProject(taskView.getProjectId());
            String message = createDeadlineNotificationMessage(taskId, project.getName());
            kafkaTemplate.send("notification-topic", new SendNotificationDto(message, userId));
            sseNotificationController.sendMessageToUser(message, username);
        } catch (EntityNotFoundException ex) {
            log.warn("Can't find task with id = {}, so notification about deadline date won't be send", taskId);
        }
    }

    private String createDeadlineNotificationMessage(Long taskId, String projectName) {
        // TODO: locale (EN, RU)?
        return String.format("Наступил дедлайн для задачи %s на проекте \"%s\"",
                taskId, projectName);
    }
}
