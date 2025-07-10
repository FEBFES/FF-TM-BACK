package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.projection.MemberIdRoleProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProjectMemberRepository extends JpaRepository<ProjectEntity, Long> {

    // TODO: create migration and drop v_user in there
    @Query(value = """
            select up.user_id as id,
                   r.name      as "roleOnProject"
            from user_project up
                inner join project_user_role pur on up.user_id = pur.user_id and up.project_id = pur.project_id
                inner join role r on pur.role_id = r.id
            where up.project_id = ?1
            """, nativeQuery = true)
    List<MemberIdRoleProjection> getProjectMembersWithRole(Long projectId);

    @Query(value = """
            select up.user_id as id,
                   r.name      as "roleOnProject"
            from user_project up
                inner join project_user_role pur on up.user_id = pur.user_id and up.project_id = pur.project_id
                inner join role r on pur.role_id = r.id
            where up.project_id = ?1 and up.user_id in (?2)
            """, nativeQuery = true)
    List<MemberIdRoleProjection> getProjectMembersWithRole(Long projectId, Set<Long> userId);

    @Query(value = """
            select up.user_id as id,
                   r.name      as "roleOnProject"
            from user_project up
                inner join project_user_role pur on up.user_id = pur.user_id and up.project_id = pur.project_id
                inner join role r on pur.role_id = r.id
            where up.project_id = ?1 and up.user_id = ?2
            """, nativeQuery = true)
    Optional<MemberIdRoleProjection> getProjectMemberWithRole(Long projectId, Long userId);
}
