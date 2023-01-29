package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.TaskEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findAllByColumnId(Pageable pageableRequest, Long columnId);
}
