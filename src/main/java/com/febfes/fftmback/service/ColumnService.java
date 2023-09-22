package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;

import java.util.List;

public interface ColumnService {

    TaskColumnEntity createColumn(TaskColumnEntity column);

    TaskColumnEntity editColumn(ColumnDto columnDto, Long columnId);

    void deleteColumn(Long id);

    void createDefaultColumnsForProject(Long projectId);

    /**
     * Return project's columns
     */
    List<TaskColumnEntity> getOrderedColumns(Long projectId);
}
