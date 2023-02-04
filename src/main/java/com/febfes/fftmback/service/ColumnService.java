package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;

public interface ColumnService {

    TaskColumnEntity createColumn(Long projectId, ColumnDto columnDto);

    void editColumn(Long id, ColumnDto columnDto);

    void deleteColumn(Long id);
}
