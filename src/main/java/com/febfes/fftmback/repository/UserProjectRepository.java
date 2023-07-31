package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.common.UserProjectId;
import com.febfes.fftmback.domain.dao.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {

    void deleteByIdProjectIdAndIdUserId(Long projectId, Long UserId);
}
