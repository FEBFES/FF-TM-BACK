package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.mapper.ColumnMapper;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.util.DateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {

    private final ColumnRepository columnRepository;
    private final DateProvider dateProvider;

    @Override
    public TaskColumnEntity createColumn(Long projectId, ColumnDto columnDto) {
        TaskColumnEntity columnEntity = columnRepository.save(
                ColumnMapper.INSTANCE.columnDtoToColumn(columnDto, projectId, dateProvider.getCurrentDate())
        );
        log.info("Saved column: {}", columnEntity);
        return columnEntity;
    }

    @Override
    public void editColumn(Long id, ColumnDto columnDto) {
        TaskColumnEntity columnEntity = columnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TaskColumnEntity.class.getSimpleName(), id));
        columnEntity.setName(columnDto.name());
        columnEntity.setColumnOrder(columnDto.columnOrder());
        columnRepository.save(columnEntity);
        log.info("Updated column: {}", columnEntity);

    }

    @Override
    public void deleteColumn(Long id) {
        if (columnRepository.existsById(id)) {
            columnRepository.deleteById(id);
            log.info("Column with id={} deleted", id);
        } else {
            throw new EntityNotFoundException(TaskColumnEntity.class.getSimpleName(), id);
        }
    }

    @Override
    public void createDefaultColumnsForProject(Long projectId) {
        List<String> defaultColumnsNames = Arrays.asList("BACKLOG", "IN PROGRESS", "REVIEW", "DONE");
        for (int i = 0; i < defaultColumnsNames.size(); i++) {
            TaskColumnEntity defaultColumn = TaskColumnEntity.builder()
                    .name(defaultColumnsNames.get(i))
                    .columnOrder(i)
                    .projectId(projectId)
                    .build();
            defaultColumn.setCreateDate(dateProvider.getCurrentDate());
            columnRepository.save(defaultColumn);
        }
        log.info("Created default columns with names: {}", defaultColumnsNames);
    }

}
