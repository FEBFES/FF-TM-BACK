package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.order.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {

    private final ColumnRepository columnRepository;
    private final OrderService<TaskColumnEntity> orderService;


    @Override
    public TaskColumnEntity createColumn(TaskColumnEntity column) {
        column.setEntityOrder(orderService.getNewOrder(column));
        TaskColumnEntity savedColumn = columnRepository.save(column);

        log.info("Saved column: {}", savedColumn);
        return savedColumn;
    }

    @Override
    public TaskColumnEntity editColumn(ColumnDto column, Long columnId) {
        TaskColumnEntity oldColumn = columnRepository.findById(columnId)
                .orElseThrow(Exceptions.columnNotFound(columnId));
        oldColumn.setName(column.name());
        TaskColumnEntity updatedColumn = columnRepository.save(oldColumn);
        orderService.editOrder(updatedColumn, column.order());
        log.info("Updated column: {}", updatedColumn);
        return updatedColumn;
    }

    @Override
    public void deleteColumn(Long id) {
        TaskColumnEntity columnEntity = columnRepository.findById(id)
                .orElseThrow(Exceptions.columnNotFound(id));
        columnRepository.delete(columnEntity);
        orderService.removeEntity(columnEntity);

        log.info("Column with id={} deleted", id);
    }

    @Override
    public void createDefaultColumnsForProject(Long projectId) {
        Arrays.stream(DefaultColumns.values())
                .map(DefaultColumns::getCaption)
                .forEach(columnName -> createColumn(TaskColumnEntity.builder()
                .name(columnName)
                .projectId(projectId)
                .build()
        ));
        log.info("Created default columns");
    }

    @Override
    public List<TaskColumnEntity> getOrderedColumns(Long projectId) {
        return columnRepository.findAllByProjectIdOrderByEntityOrder(projectId);
    }

}
