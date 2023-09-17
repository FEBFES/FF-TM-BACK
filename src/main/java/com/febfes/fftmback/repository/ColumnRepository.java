package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnRepository extends JpaRepository<TaskColumnEntity, Long> {

    List<TaskColumnEntity> findAllByProjectIdOrderByEntityOrder(Long projectId);
}
