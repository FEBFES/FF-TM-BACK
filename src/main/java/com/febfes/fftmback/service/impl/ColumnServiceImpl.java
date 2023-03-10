package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.TaskService;
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
    private final TaskService taskService;

    private static final List<String> DEFAULT_COLUMNS = List.of("BACKLOG", "IN PROGRESS", "REVIEW", "DONE");

    @Override
    public TaskColumnEntity createColumn(TaskColumnEntity column) {
        column.setCreateDate(DateUtils.getCurrentDate());
        TaskColumnEntity savedColumn = columnRepository.save(column);
        columnRepository.updateChildColumn(
                savedColumn.getId(),
                savedColumn.getChildTaskColumnId(),
                savedColumn.getProjectId()
        );
        log.info("Saved column: {}", savedColumn);
        return savedColumn;
    }

    @Override
    public void editColumn(TaskColumnEntity column) {
        TaskColumnEntity oldColumn = columnRepository.findById(column.getId())
                .orElseThrow(() -> new EntityNotFoundException(TaskColumnEntity.class.getSimpleName(), column.getId()));
        oldColumn.setName(column.getName());
        if (!Objects.equals(oldColumn.getChildTaskColumnId(), column.getChildTaskColumnId())) {
            columnRepository.updateChildColumn(
                    oldColumn.getChildTaskColumnId(),
                    oldColumn.getId(),
                    oldColumn.getProjectId()
            );
            columnRepository.updateChildColumn(
                    oldColumn.getId(),
                    column.getChildTaskColumnId(),
                    oldColumn.getProjectId()
            );
            oldColumn.setChildTaskColumnId(column.getChildTaskColumnId());
        }
        columnRepository.save(oldColumn);
        log.info("Updated column: {}", oldColumn);

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
        DEFAULT_COLUMNS.forEach(columnName -> createColumn(TaskColumnEntity
                .builder()
                .name(columnName)
                .projectId(projectId)
                .build()
        ));
        log.info("Created default columns with names: {}", DEFAULT_COLUMNS);
    }

    @Override
    public List<TaskColumnEntity> getColumnListWithOrder(Long projectId, String taskFilter) {
        Map<Long, TaskColumnEntity> childIdToColumnEntity = columnRepository
                .findAllByProjectId(projectId)
                .stream()
                /* TODO: as I understand it, there will be an extra request to the database. Gotta do something about it
                 */
                .peek(column -> column.setTaskEntityList(taskService.getTasks(column.getId(), taskFilter)))
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
