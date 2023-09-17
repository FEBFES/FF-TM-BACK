package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.projection.ColumnProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnRepository extends JpaRepository<TaskColumnEntity, Long> {

    List<TaskColumnEntity> findAllByProjectId(Long projectId);

    @Query(
            value = """
                    select tk.id, tk.name, tk.create_date, eo.index
                    from task_column tk
                    	inner join entity_order eo on eo.user_id = ?2 and eo.entity_type = ?3 and eo.entity_id = tk.id
                    where tk.project_id = ?1
                    order by eo.index
                    """,
            nativeQuery = true
    )
    List<ColumnProjection> getColumns(Long projectId, Long userId, String entityType);

    /**
     * Change id of a child column from oldChildId to newChildId
     */
//    @Modifying
//    @Query("UPDATE TaskColumnEntity " +
//           "SET childTaskColumnId = :newChildId " +
//           "WHERE " +
//           "((:newChildId IS NULL AND id IS NOT NULL) OR (:newChildId IS NOT NULL AND id != :newChildId)) AND " +
//           "(" +
//           "(:oldChildId IS NOT NULL AND childTaskColumnId = :oldChildId) OR " +
//           "(:oldChildId IS NULL AND childTaskColumnId IS NULL)" +
//           ") AND " +
//           "projectId = :projectId")
//    void updateChildColumn(Long newChildId, Long oldChildId, Long projectId);
}
