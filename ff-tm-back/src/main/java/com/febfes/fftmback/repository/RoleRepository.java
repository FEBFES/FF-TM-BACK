package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.dao.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(RoleName name);

    @Query(value = """
            select r.*
            from user_project up
                inner join project_user_role pur on up.user_id = pur.user_id and up.project_id = pur.project_id
                inner join role r on pur.role_id = r.id
            where up.project_id = ?1 and up.user_id = ?2
            """, nativeQuery = true)
    Optional<RoleEntity> getUserRoleOnProject(Long projectId, Long userId);

    @Modifying
    @Query(value = """
        INSERT INTO project_user_role (role_id, user_id, project_id)
        SELECT r.id, :userId, :projectId
        FROM role r
        WHERE r.name = :roleName
          AND EXISTS (
              SELECT 1 FROM user_project up
              WHERE up.project_id = :projectId
                AND up.user_id = :userId
          )
        ON CONFLICT (project_id, user_id)
        DO UPDATE SET role_id = EXCLUDED.role_id
        """, nativeQuery = true)
    int changeUserRoleOnProject(@Param("projectId") Long projectId,
                                @Param("userId") Long userId,
                                @Param("roleName") String roleName);
}
