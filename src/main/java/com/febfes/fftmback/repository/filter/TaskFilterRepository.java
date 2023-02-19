package com.febfes.fftmback.repository.filter;

import com.febfes.fftmback.domain.common.query.FilterRequest;
import com.febfes.fftmback.domain.common.query.FilterSpecification;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskFilterRepository {

    private final TaskRepository taskRepository;

    public List<TaskEntity> getQueryResult(List<FilterRequest> filters) {
        if (filters.isEmpty()) {
            return taskRepository.findAll();
        }
        FilterSpecification<TaskEntity> filterSpecification = new FilterSpecification<>(filters);
        return taskRepository.findAll(filterSpecification);
    }

    public List<TaskEntity> getQueryResultWithPagination(List<FilterRequest> filters, Pageable pageableRequest) {
        if (filters.isEmpty()) {
            return taskRepository.findAll(pageableRequest).getContent();
        }
        FilterSpecification<TaskEntity> filterSpecification = new FilterSpecification<>(filters);
        return taskRepository.findAll(filterSpecification, pageableRequest).getContent();
    }

}
