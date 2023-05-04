package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.TaskFileEntity;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.dto.parameter.TaskParameters;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TaskService {

    List<TaskEntity> getTasks(int page, int limit, Long columnId, String filter);

    List<TaskEntity> getTasks(Long columnId, String filter);

    List<TaskEntity> getTasks(String filter);

    TaskEntity getTaskById(Long id);

    TaskEntity createTask(TaskEntity task, String username);

    TaskEntity updateTask(Long id, Long projectId, Long columnId, TaskDto taskDto);

    void deleteTask(Long id);

    /*TODO: мб создать отдельный сервис для task files? И в него запихнуть следующие 7 методов*/
    void saveFileTasks(TaskParameters pathVars, Long userId, MultipartFile[] files);

    List<TaskFileEntity> getTaskFiles(Long taskId);

    TaskFileEntity getTaskFile(TaskParameters pathVars, String fileId);

    byte[] getTaskFileContent(TaskParameters pathVars, String fileId) throws IOException;

    List<TaskDto> updateTasksWithFiles(List<TaskEntity> tasks);

    TaskDto updateTaskWithFiles(TaskEntity task);

    void deleteTaskFile(Long taskId, Long taskFileId);
}
