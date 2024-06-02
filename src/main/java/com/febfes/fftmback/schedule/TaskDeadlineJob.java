package com.febfes.fftmback.schedule;

import com.febfes.fftmback.controller.SseNotificationController;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.service.NotificationService;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.service.project.ProjectManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.febfes.fftmback.service.impl.TaskServiceDecorator.TASK_ID;
import static com.febfes.fftmback.service.impl.TaskServiceDecorator.USER_ID;

@Slf4j
@Component
@RequiredArgsConstructor
//@NoArgsConstructor(force = true)
@Transactional
@DisallowConcurrentExecution
//public class TaskDeadlineJob extends QuartzJobBean {
public class TaskDeadlineJob implements Job {

    private final NotificationService notificationService;
    private final UserService userService;
    private final SseNotificationController sseNotificationController;

    @Qualifier("projectManagementServiceDecorator")
    private final ProjectManagementService projectManagementService;

    @Qualifier("taskServiceDecorator")
    private final TaskService taskService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Long userId = jobDataMap.getLong(USER_ID);
        Long taskId = jobDataMap.getLong(TASK_ID);
        log.info("USER ID: " + userId);
        try {
            TaskView taskView = taskService.getTaskById(taskId);
            ProjectEntity project = projectManagementService.getProject(taskView.getProjectId());
            String message = createDeadlineNotificationMessage(taskId, project.getName());
            notificationService.createNotification(message, userId);
            String username = userService.getUserById(userId).getUsername();
            sseNotificationController.sendMessageToTheUser(message, username);
        } catch (EntityNotFoundException ex) {
            log.warn("Can't find task with id = {}, so notification about deadline date won't be send", taskId);
        }
    }

//    @Override
//    protected void executeInternal(JobExecutionContext context) {
//        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
//        Long userId = jobDataMap.getLong(USER_ID);
//        Long taskId = jobDataMap.getLong(TASK_ID);
//    }

    private String createDeadlineNotificationMessage(Long taskId, String projectName) {
        return String.format("The deadline has arrived for completing task with ID %s on the \"%s\" project",
                taskId, projectName);
    }
}
