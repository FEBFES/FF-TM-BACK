package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.projection.ColumnProjection;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.order.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {

    private final ColumnRepository columnRepository;
    private final OrderService<TaskColumnEntity> orderService;

    private static final List<String> DEFAULT_COLUMNS = List.of("BACKLOG", "IN PROGRESS", "REVIEW", "DONE");

    @Override
    public TaskColumnEntity createColumn(TaskColumnEntity column, Long userId) {
        TaskColumnEntity savedColumn = columnRepository.save(column);
        orderService.addEntity(savedColumn, userId);
//        columnRepository.updateChildColumn(
//                savedColumn.getId(),
//                savedColumn.getChildTaskColumnId(),
//                savedColumn.getProjectId()
//        );

        log.info("Saved column: {}", savedColumn);
        return savedColumn;
    }

    @Override
    public TaskColumnEntity editColumn(ColumnDto column, Long columnId, Long userId) {
        TaskColumnEntity oldColumn = columnRepository.findById(columnId)
                .orElseThrow(() -> new EntityNotFoundException(TaskColumnEntity.ENTITY_NAME, columnId));
        oldColumn.setName(column.name());
        orderService.editIndex(oldColumn, column.index(), userId);
//        if (!Objects.equals(oldColumn.getChildTaskColumnId(), column.getChildTaskColumnId())) {
//            columnRepository.updateChildColumn(
//                    oldColumn.getChildTaskColumnId(),
//                    oldColumn.getId(),
//                    oldColumn.getProjectId()
//            );
//            columnRepository.updateChildColumn(
//                    oldColumn.getId(),
//                    column.getChildTaskColumnId(),
//                    oldColumn.getProjectId()
//            );
//            oldColumn.setChildTaskColumnId(column.getChildTaskColumnId());
//        }
        TaskColumnEntity updatedColumn = columnRepository.save(oldColumn);
        log.info("Updated column: {}", updatedColumn);
        return updatedColumn;
    }

    @Override
    public void deleteColumn(Long id, Long userId) {
        TaskColumnEntity columnEntity = columnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TaskColumnEntity.ENTITY_NAME, id));
//        columnRepository.updateChildColumn(
//                columnEntity.getChildTaskColumnId(),
//                columnEntity.getId(),
//                columnEntity.getProjectId()
//        );
        columnRepository.delete(columnEntity);
        orderService.removeEntity(columnEntity, userId);

        log.info("Column with id={} deleted", id);
    }

    @Override
    public void createDefaultColumnsForProject(Long projectId, Long userId) {
        DEFAULT_COLUMNS.forEach(columnName -> createColumn(TaskColumnEntity.builder()
                .name(columnName)
                .projectId(projectId)
                .build(),
                userId
        ));
        log.info("Created default columns with names: {}", DEFAULT_COLUMNS);
    }

//    @Override
//    public List<TaskColumnEntity> getOrderedColumns(Long projectId) {
//        Map<Long, TaskColumnEntity> childIdToColumnEntity = columnRepository
//                .findAllByProjectId(projectId)
//                .stream()
//                .collect(Collectors.toMap(TaskColumnEntity::getChildTaskColumnId, Function.identity()));
//        List<TaskColumnEntity> result = new ArrayList<>();
//        Long currentColumnId = null;
//        for (int i = childIdToColumnEntity.size(); i > 0; i--) {
//            TaskColumnEntity column = childIdToColumnEntity.get(currentColumnId);
//            column.setColumnOrder(i);
//            result.add(column);
//            currentColumnId = column.getId();
//        }
//        Collections.reverse(result);
//        return result;
//    }
    @Override
    public List<ColumnProjection> getOrderedColumns(Long projectId, Long userId) {
        return columnRepository.getColumns(projectId, userId, EntityType.COLUMN.name());
    }

}
