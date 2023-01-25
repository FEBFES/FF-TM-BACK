package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.util.DateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final DateProvider dateProvider;

    public TaskColumnEntity createColumn(Long projectId, ColumnDto columnDto) {
        return columnRepository.save(createColumnEntity(
                columnDto.getName(),
                columnDto.getDescription(),
                columnDto.getColumnOrder(),
                projectId
        ));
    }

    public List<TaskColumnEntity> getColumns(Long projectId) {
        // TODO add pagination
        return columnRepository.findColumnEntitiesByProjectId(projectId);
    }

    public boolean editColumn(Long id, ColumnDto columnDto) {
        Optional<TaskColumnEntity> columnEntity = columnRepository.findById(id);
        columnEntity.ifPresent(column -> {
            column.setName(columnDto.getName());
            column.setDescription(columnDto.getDescription());
            column.setColumnOrder(columnDto.getColumnOrder());
            columnRepository.save(column);
        });
        return columnEntity.isPresent();
    }

    public boolean deleteColumn(Long id) {
        if (columnRepository.existsById(id)) {
            columnRepository.deleteById(id);
        }
        return true;
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

    public static ColumnDto mapToColumnDto(TaskColumnEntity taskColumnEntity) {
        return new ColumnDto(
                taskColumnEntity.getId(),
                taskColumnEntity.getName(),
                taskColumnEntity.getDescription(),
                taskColumnEntity.getCreateDate(),
                taskColumnEntity.getColumnOrder(),
                taskColumnEntity.getProjectId()
        );
    }
}
