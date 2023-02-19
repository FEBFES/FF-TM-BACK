package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.mapper.ColumnMapper;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.util.DateUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {

    private final ColumnRepository columnRepository;

    private static final List<String> DEFAULT_COLUMNS = List.of("BACKLOG", "IN PROGRESS", "REVIEW", "DONE");

    @Override
    public TaskColumnEntity createColumn(
            Long projectId,
            ColumnDto columnDto
    ) {
        TaskColumnEntity columnEntity = columnRepository.save(
                ColumnMapper.INSTANCE.columnDtoToColumn(columnDto, projectId, DateUtils.getCurrentDate())
        );
        columnRepository.updateChildColumn(columnEntity.getId(), columnEntity.getChildTaskColumnId(), projectId);
        log.info("Saved column: {}", columnEntity);
        return columnEntity;
    }

    @Override
    public void editColumn(
            Long id,
            ColumnDto columnDto
    ) {
        TaskColumnEntity columnEntity = columnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TaskColumnEntity.class.getSimpleName(), id));
        columnEntity.setName(columnDto.name());
        if (!Objects.equals(columnEntity.getChildTaskColumnId(), columnDto.childTaskColumnId())) {
            columnRepository.updateChildColumn(
                    columnEntity.getChildTaskColumnId(),
                    columnEntity.getId(),
                    columnEntity.getProjectId()
            );
            columnRepository.updateChildColumn(
                    columnEntity.getId(),
                    columnDto.childTaskColumnId(),
                    columnEntity.getProjectId()
            );
            columnEntity.setChildTaskColumnId(columnDto.childTaskColumnId());
        }
        columnRepository.save(columnEntity);
        log.info("Updated column: {}", columnEntity);

    }

    @Override
    public void deleteColumn(Long id) {
        TaskColumnEntity columnEntity = columnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TaskColumnEntity.class.getSimpleName(), id));
        columnRepository.updateChildColumn(
                columnEntity.getChildTaskColumnId(),
                columnEntity.getId(),
                columnEntity.getProjectId()
        );
        columnRepository.delete(columnEntity);
        log.info("Column with id={} deleted", id);
    }

    @Override
    public void createDefaultColumnsForProject(Long projectId) {
        DEFAULT_COLUMNS.forEach(columnName -> createColumn(projectId, new ColumnDto(columnName)));
        log.info("Created default columns with names: {}", DEFAULT_COLUMNS);
    }

    @Override
    public List<TaskColumnEntity> getColumnListWithOrder(Long projectId) {
        Map<Long, TaskColumnEntity> childIdToColumnEntity = columnRepository
                .findAllByProjectId(projectId)
                .stream()
                .collect(Collectors.toMap(TaskColumnEntity::getChildTaskColumnId, Function.identity()));
        List<TaskColumnEntity> result = new ArrayList<>();
        Long currentColumnId = null;
        for (int i = childIdToColumnEntity.size(); i > 0; i--) {
            TaskColumnEntity column = childIdToColumnEntity.get(currentColumnId);
            column.setColumnOrder(i);
            result.add(column);
            currentColumnId = column.getId();
        }

        Collections.reverse(result);
        return result;
    }

}
