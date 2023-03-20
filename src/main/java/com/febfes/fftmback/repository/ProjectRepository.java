package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    List<ProjectEntity> findAllByOwnerId(Long ownerId);

    @Query(
            nativeQuery = true,
            value = "SELECT COUNT(*) > 0  FROM favourite_project WHERE project_id = :projectId AND user_id = :userId"
    )
    Boolean isProjectFavourite(Long projectId, Long userId);

    @Query(
            nativeQuery = true,
            value = "SELECT project_id FROM favourite_project WHERE user_id = :userId"
    )
    Set<Long> findAllFavouriteProjectIdsForUser(Long userId);

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

}
