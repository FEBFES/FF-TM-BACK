package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.projection.ProjectProjection;
import com.febfes.fftmback.domain.projection.ProjectWithMembersProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT COUNT(*) > 0 FROM favourite_project WHERE project_id = :projectId AND user_id = :userId"
    )
    Boolean isProjectFavourite(Long projectId, Long userId);

    @Query(
            value = "SELECT COUNT(*) > 0 FROM ProjectEntity P " +
                    "INNER JOIN TaskColumnEntity TC ON TC.id = :columnId AND TC.projectId = :projectId " +
                    "WHERE P.id = :projectId"
    )
    boolean doesProjectEntityContainColumn(Long projectId, Long columnId);

    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO favourite_project (project_id, user_id) VALUES (:projectId, :userId)"
    )
    void addProjectToFavourite(Long projectId, Long userId);

    @Modifying
    @Query(
            nativeQuery = true,
            value = "DELETE FROM favourite_project WHERE project_id = :projectId AND user_id = :userId"
    )
    void removeProjectFromFavourite(Long projectId, Long userId);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT P.id,
                           P.create_date            AS "createDate",
                           P.name                   AS "name",
                           P.description            AS "description",
                           P.owner_id               AS "ownerId",
                           (FP.user_id is not null) AS "isFavourite",
                           R.name AS "roleName",
                           R.description AS "roleDescription"
                    FROM project P
                    		LEFT JOIN favourite_project FP ON P.id = FP.project_id AND FP.user_id = :userId
                    		LEFT JOIN project_user_role PUR ON P.id = PUR.project_id AND PUR.user_id = :userId
                    		LEFT JOIN role R ON PUR.role_id = R.id
                    WHERE P.id = :projectId
                    """
    )
    Optional<ProjectWithMembersProjection> getProjectByIdAndUserId(Long projectId, Long userId);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT P.id,
                           P.create_date            AS "createDate",
                           P.name                   AS "name",
                           P.description            AS "description",
                           P.owner_id               AS "ownerId",
                           (FP.user_id is not null) AS "isFavourite"
                    FROM project P
                    		LEFT JOIN favourite_project FP ON P.id = FP.project_id AND FP.user_id = :userId
                    		INNER JOIN user_project UP ON UP.project_id = P.id AND UP.user_id = :userId
                    ORDER BY "isFavourite" desc, ?#{#sort}
                    """
    )
    List<ProjectProjection> getUserProjects(Long userId, Sort sort);
}
