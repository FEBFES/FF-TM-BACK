package com.example.fftmback.repository;

import com.example.fftmback.domain.ColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnRepository extends JpaRepository<ColumnEntity, Long> {

    @Query(value = "SELECT c FROM ColumnEntity c WHERE c.projectId = :projectId")
    List<ColumnEntity> findColumnEntitiesByProjectId(@Param("projectId") Long projectId);
}
