package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.projection.ColumnProjection;
import com.febfes.fftmback.dto.ColumnDto;

import java.util.List;

public interface ColumnService {

    TaskColumnEntity createColumn(TaskColumnEntity column, Long userId);

    TaskColumnEntity editColumn(ColumnDto columnDto, Long columnId, Long userId);

    void deleteColumn(Long id, Long userId);

    void createDefaultColumnsForProject(Long projectId, Long userId);

    /**
     * Return project's columns with order
     */
    List<ColumnProjection> getOrderedColumns(Long projectId, Long userId);
}
