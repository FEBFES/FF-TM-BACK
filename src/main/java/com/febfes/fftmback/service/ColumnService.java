package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;

import java.util.List;

public interface ColumnService {

    TaskColumnEntity createColumn(Long projectId, ColumnDto columnDto);

    void editColumn(Long id, ColumnDto columnDto);

    void deleteColumn(Long id);

    void createDefaultColumnsForProject(Long projectId);

    /**
     * Return project's columns with order
     */
    List<TaskColumnEntity> getColumnListWithOrder(Long projectId, String taskFilter);
}
