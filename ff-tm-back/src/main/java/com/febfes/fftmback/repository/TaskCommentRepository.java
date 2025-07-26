package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.TaskCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, Long> {

    @Query(value = """
            SELECT tc.id, tc.create_date, tc.creator_id, tc.task_id, tc.text
            FROM task_comment tc
            WHERE tc.task_id = :taskId
            ORDER BY tc.create_date
            """, nativeQuery = true)
    List<TaskCommentEntity> findCommentsByTaskId(@Param("taskId") Long taskId);
}
