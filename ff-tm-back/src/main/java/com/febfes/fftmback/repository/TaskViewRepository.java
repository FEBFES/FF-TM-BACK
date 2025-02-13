package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.TaskView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskViewRepository extends JpaRepository<TaskView, Long>, JpaSpecificationExecutor<TaskView> {

}
