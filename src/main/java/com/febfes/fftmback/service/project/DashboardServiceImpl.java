package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.mapper.ColumnWithTasksMapper;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.TaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final ColumnService columnService;
    private final TaskService taskService;
    private final ColumnWithTasksMapper columnWithTasksMapper;

    @Override
    public DashboardDto getDashboard(Long id, TaskSpec taskSpec) {
        List<TaskColumnEntity> columns = columnService.getOrderedColumns(id);
        Map<Long, List<TaskView>> columnIdToTaskListMap = taskService.getTasks(
                columns.stream().map(TaskColumnEntity::getId).collect(Collectors.toSet()),
                taskSpec
        )
                .stream()
                .collect(Collectors.groupingBy(TaskView::getColumnId, Collectors.toList()));
        List<ColumnWithTasksDto> columnsWithTasks = columns.stream()
                .map(column -> columnWithTasksMapper.columnToColumnWithTasksDto(
                        column,
                        columnIdToTaskListMap.getOrDefault(column.getId(), Collections.emptyList())
                ))
                .toList();
        log.info("Received dashboard with id={}", id);
        return new DashboardDto(columnsWithTasks);
    }
}
