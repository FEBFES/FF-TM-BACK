package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;

import java.util.List;

public interface ColumnService {

    TaskColumnEntity createColumn(TaskColumnEntity column);

    TaskColumnEntity editColumn(TaskColumnEntity columnDto);

    void deleteColumn(Long id);

    void createDefaultColumnsForProject(Long projectId);

    /**
     * Return project's columns with order
     */
    List<TaskColumnEntity> getOrderedColumns(Long projectId);
}
