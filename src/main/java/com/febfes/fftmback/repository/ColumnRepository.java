package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.TaskColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnRepository extends JpaRepository<TaskColumnEntity, Long> {

    @Query(value = "SELECT c FROM TaskColumnEntity c WHERE c.projectId = :projectId")
    List<TaskColumnEntity> findColumnEntitiesByProjectId(@Param("projectId") Long projectId);
}
