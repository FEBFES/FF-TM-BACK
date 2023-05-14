package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnRepository extends JpaRepository<TaskColumnEntity, Long>, JpaSpecificationExecutor<TaskColumnEntity> {

    /**
     * Change id of a child column from oldChildId to newChildId
     */
    @Modifying
    @Query("UPDATE TaskColumnEntity " +
            "SET childTaskColumnId = :newChildId " +
            "WHERE " +
            "((:newChildId IS NULL AND id IS NOT NULL) OR (:newChildId IS NOT NULL AND id != :newChildId)) AND " +
            "(" +
            "(:oldChildId IS NOT NULL AND childTaskColumnId = :oldChildId) OR " +
            "(:oldChildId IS NULL AND childTaskColumnId IS NULL)" +
            ") AND " +
            "projectId = :projectId")
    void updateChildColumn(Long newChildId, Long oldChildId, Long projectId);
}
