package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.UserView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface UserViewRepository extends JpaRepository<UserView, Long>, JpaSpecificationExecutor<UserView> {

    @Query("SELECT uv.userPic.fileUrn FROM UserView uv WHERE uv.id = :userId")
    String getUserPicById(Long userId);

    @Query(
            value = "SELECT r.name FROM role r WHERE r.id=(" +
                    "SELECT pur.role_id FROM project_user_role pur WHERE pur.user_id = ?1 AND pur.project_id = ?2)",
            nativeQuery = true
    )
    String getUserRole(Long userId, Long projectId);
}
