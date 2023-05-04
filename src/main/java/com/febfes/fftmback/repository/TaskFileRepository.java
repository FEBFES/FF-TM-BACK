package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.TaskFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskFileRepository extends JpaRepository<TaskFileEntity, Long> {

    Optional<TaskFileEntity> findByFileUrn(String fileUrn);

    List<TaskFileEntity> getByTaskId(Long taskId);
}
