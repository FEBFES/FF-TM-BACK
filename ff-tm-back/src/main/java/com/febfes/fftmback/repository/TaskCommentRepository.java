package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.TaskCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, Long> {
}
