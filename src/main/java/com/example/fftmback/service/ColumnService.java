package com.example.fftmback.service;

import com.example.fftmback.domain.ColumnEntity;
import com.example.fftmback.dto.ColumnDto;
import com.example.fftmback.repository.ColumnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColumnService {

    @Autowired
    private ColumnRepository columnRepository;


    public ColumnEntity createColumn(Long projectId, ColumnDto columnDto) {
        return columnRepository.save(createColumnEntity(
                columnDto.getName(),
                columnDto.getDescription(),
                columnDto.getColumnOrder(),
                projectId
        ));
    }

    public List<ColumnEntity> getColumns(Long projectId) {
        // TODO add pagination
        return columnRepository.findColumnEntitiesByProjectId(projectId);
    }

    public boolean editColumn(Long id, ColumnDto columnDto) {
        Optional<ColumnEntity> columnEntity = columnRepository.findById(id);
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

    private ColumnEntity createColumnEntity(String name, String description, Integer columnOrder, Long projectId) {
        ColumnEntity columnEntity = new ColumnEntity();
        columnEntity.setName(name);
        columnEntity.setDescription(description);
        columnEntity.setColumnOrder(columnOrder);
        columnEntity.setCreateDate(new Date());
        columnEntity.setProjectId(projectId);
        return columnEntity;
    }

    public static ColumnDto mapToColumnDto(ColumnEntity columnEntity) {
        return new ColumnDto(
                columnEntity.getId(),
                columnEntity.getName(),
                columnEntity.getDescription(),
                columnEntity.getColumnOrder()
        );
    }
}
