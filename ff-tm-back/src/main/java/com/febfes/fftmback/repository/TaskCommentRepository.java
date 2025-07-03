package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.TaskCommentEntity;
import com.febfes.fftmback.domain.projection.TaskCommentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, Long> {

    @Query(value = """
            SELECT tc.id, tc.create_date, tc.creator_id, tc.task_id, tc.text, u.username AS creator_name
            FROM task_comment tc
            LEFT JOIN user_entity u ON tc.creator_id = u.id
            WHERE tc.task_id = :taskId
            ORDER BY tc.create_date
            """, nativeQuery = true)
    List<TaskCommentProjection> findCommentsWithCreatorNameByTaskId(@Param("taskId") Long taskId);

    @Query(value = """
            SELECT tc.id, tc.creator_id, tc.task_id, tc.text, u.username AS creator_name
            FROM task_comment tc
            LEFT JOIN user_entity u ON tc.creator_id = u.id
            WHERE tc.id = :id
            """, nativeQuery = true)
    TaskCommentProjection findOneWithCreatorNameById(@Param("id") Long id);
}
