package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.mapper.ColumnWithTasksMapper;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.TaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final ColumnService columnService;

    @Qualifier("taskServiceDecorator")
    private final TaskService taskService;

    @Override
    public DashboardDto getDashboard(Long id, TaskSpec taskSpec) {
        List<ColumnWithTasksDto> columnsWithTasks = columnService.getOrderedColumns(id)
                .stream()
                .map(column -> {
                    List<TaskView> filteredTasks = taskService.getTasks(column.getId(), taskSpec);
                    return ColumnWithTasksMapper.INSTANCE.columnToColumnWithTasksDto(column, filteredTasks);
                })
                .toList();
        log.info("Received dashboard with id={}", id);
        return new DashboardDto(columnsWithTasks);
    }
}
