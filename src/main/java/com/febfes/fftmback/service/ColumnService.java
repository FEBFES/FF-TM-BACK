package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.TaskColumnEntity;

import java.util.List;

public interface ColumnService {

    TaskColumnEntity createColumn(TaskColumnEntity column);

    void editColumn(TaskColumnEntity columnDto);

    void deleteColumn(Long id);

    void createDefaultColumnsForProject(Long projectId);

    /**
     * Return project's columns with order
     */
    List<TaskColumnEntity> getColumnListWithOrder(Long projectId);
}
