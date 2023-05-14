package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.query.FilterRequest;
import com.febfes.fftmback.domain.common.query.FilterSpecification;
import com.febfes.fftmback.domain.common.query.Operator;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.TaskService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
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
                .orElseThrow(() -> new EntityNotFoundException(TaskColumnEntity.NAME, column.getId()));
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
                .orElseThrow(() -> new EntityNotFoundException(TaskColumnEntity.NAME, id));
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
        FilterSpecification<TaskView> filter = taskService.makeTasksFilter(taskFilter);
        List<FilterRequest> columnFilters = new ArrayList<>();
        columnFilters.add(FilterRequest.builder()
                .property("projectId")
                .operator(Operator.EQUAL)
                .value(projectId)
                .build());
        Specification<TaskColumnEntity> taskColumnSpecification = createTaskFilterForColumn(filter, columnFilters);
        Map<Long, TaskColumnEntity> childIdToColumnEntity = columnRepository
                .findAll(taskColumnSpecification)
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

    private Specification<TaskColumnEntity> createTaskFilterForColumn(
            FilterSpecification<TaskView> taskFilterSpec,
            List<FilterRequest> columnFilters
    ) {
        return (root, query, cb) -> {
            FilterSpecification<TaskColumnEntity> filterSpecification = new FilterSpecification<>(columnFilters);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(filterSpecification.toPredicate(root, query, cb));
            Root<TaskColumnEntity> taskColumnEntityRoot = query.from(TaskColumnEntity.class);
            Join<TaskColumnEntity, TaskView> taskColumnJoin = taskColumnEntityRoot.join("taskList", JoinType.LEFT);

            for (FilterRequest taskFilter : taskFilterSpec.filterRequests()) {
//                taskColumnJoin = root.join("taskList", JoinType.INNER);
//                taskColumnEntityRoot = query.from(TaskColumnEntity.class);
//                taskColumnEntityRoot.join("taskList", JoinType.LEFT);
                FilterSpecification.setFieldTypeToFilter(taskFilter);
//                Predicate taskPredicate = taskFilter.getOperator().build(taskColumnEntityRoot, cb, taskFilter, predicate);
//                predicates.add(taskPredicate);
//                predicates.add(criteriaBuilder.equal(taskColumnJoin.get(Ingredient_.PRODUCT), product));
//                predicates.add(cb.equal(taskColumnEntityRoot.get("taskList").get(taskFilter.getProperty()), taskFilter.getValue()));
                predicates.add(cb.equal(taskColumnJoin.get(taskFilter.getProperty()), taskFilter.getValue()));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

}
