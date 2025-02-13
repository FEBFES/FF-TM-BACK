package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.TaskTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskTypeRepository extends JpaRepository<TaskTypeEntity, Long> {
    List<TaskTypeEntity> findAllByProjectId(Long projectId);

    Optional<TaskTypeEntity> findByNameAndProjectId(String name, Long projectId);

    void deleteAllByProjectId(Long projectId);
}
