package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.UserView;
import com.febfes.fftmback.domain.projection.MemberProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserViewRepository extends JpaRepository<UserView, Long>, JpaSpecificationExecutor<UserView> {

    @Query(
            value = """
                    select u.id, u.email, u.username, u."firstName", u."lastName", u."displayName", fe.file_urn as "userPic", r.name as "roleOnProject"
                    from v_user u
                    	inner join user_project up on u.id = up.user_id
                    	inner join project_user_role pur on up.user_id = pur.user_id and up.project_id = pur.project_id
                    	inner join role r on pur.role_id=r.id
                    	left join file_entity fe on fe.id=u."userPicId"
                    where up.project_id = ?1
                    """,
            nativeQuery = true
    )
    List<MemberProjection> getProjectMembersWithRole(Long projectId);

    @Query(
            value = """
                    select u.id,
                           u.email,
                           u.username,
                           u."firstName",
                           u."lastName",
                           u."displayName",
                           fe.file_urn as "userPic",
                           r.name      as "roleOnProject"
                    from v_user u
                        inner join user_project up on u.id = up.user_id
                        inner join project_user_role pur on up.user_id = pur.user_id and up.project_id = pur.project_id
                        inner join role r on pur.role_id = r.id
                        left join file_entity fe on fe.id = u."userPicId"
                    where up.project_id = ?1 and up.user_id in (?2)
                   """,
            nativeQuery = true
    )
    List<MemberProjection> getProjectMembersWithRole(Long projectId, Set<Long> userId);

    @Query(
            value = """
                    select u.id, u.email, u.username, u."firstName", u."lastName", u."displayName", fe.file_urn as "userPic", r.name as "roleOnProject"
                    from v_user u
                    	inner join user_project up on u.id = up.user_id
                    	inner join project_user_role pur on up.user_id = pur.user_id and up.project_id = pur.project_id
                    	inner join role r on pur.role_id=r.id
                    	left join file_entity fe on fe.id=u."userPicId"
                    where up.project_id = ?1 and up.user_id = ?2
                    """,
            nativeQuery = true
    )
    Optional<MemberProjection> getProjectMemberWithRole(Long projectId, Long userId);
}
