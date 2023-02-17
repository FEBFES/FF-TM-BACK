package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;

import java.util.List;

public interface ColumnService {

    TaskColumnEntity createColumn(Long projectId, ColumnDto columnDto);

    void editColumn(Long id, ColumnDto columnDto);

    void deleteColumn(Long id);

    void createDefaultColumnsForProject(Long projectId);

    /**
     * Возвращает колонки проекта, с порядком
     */
    List<TaskColumnEntity> getColumnListWithOrder(Long projectId);
}
