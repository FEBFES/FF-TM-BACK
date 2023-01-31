package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.util.DateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final DateProvider dateProvider;

    public TaskColumnEntity createColumn(Long projectId, ColumnDto columnDto) {
        return columnRepository.save(createColumnEntity(
                columnDto.name(),
                columnDto.description(),
                columnDto.columnOrder(),
                projectId
        ));
    }

    public void editColumn(Long projectId, Long id, ColumnDto columnDto) {
        TaskColumnEntity columnEntity = columnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TaskColumnEntity.class.getSimpleName(), id));
        columnEntity.setName(columnDto.name());
        columnEntity.setDescription(columnDto.description());
        columnEntity.setColumnOrder(columnDto.columnOrder());
        columnEntity.setProjectId(projectId);
        columnRepository.save(columnEntity);
    }

    public void deleteColumn(Long id) {
        if (columnRepository.existsById(id)) {
            columnRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(TaskColumnEntity.class.getSimpleName(), id);
        }
    }

    private TaskColumnEntity createColumnEntity(String name, String description, Integer columnOrder, Long projectId) {
        TaskColumnEntity taskColumnEntity = new TaskColumnEntity();
        taskColumnEntity.setName(name);
        taskColumnEntity.setDescription(description);
        taskColumnEntity.setColumnOrder(columnOrder);
        taskColumnEntity.setCreateDate(dateProvider.getCurrentDate());
        taskColumnEntity.setProjectId(projectId);
        return taskColumnEntity;
    }

}
