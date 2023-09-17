package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.EntityOrder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntityOrderRepository extends JpaRepository<EntityOrder, Long> {

    Optional<EntityOrder> findByEntityIdAndEntityTypeAndUserId(Long entityId, EntityType entityType, Long userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO entity_order (index, user_id, entity_type, entity_id) " +
                   "VALUES (COALESCE((SELECT MAX(eo.index) FROM entity_order eo WHERE eo.user_id=:userId " +
                   "AND eo.entity_type=:entityType AND eo.entity_id=:entityId), 0) + 1, " +
                   ":userId, :entityType, :entityId)", nativeQuery = true)
    void addTop(Long userId, String entityType, Long entityId);

    /**
     * When we change index of entity, we need also to change the index of further records.
     * This function must be called with the updated index of entityOrder
     *
     * @param entityOrder   updated entity order
     * @param previousIndex previous index of updated entity order
     */
    @Modifying
    @Transactional
    @Query("UPDATE EntityOrder e SET e.index = e.index + 1 " +
           "WHERE e.index >= :#{#entityOrder.index} AND e.index < :previousIndex " +
           "AND e.id != :#{#entityOrder.id}")
    void updateFurtherRecords(EntityOrder entityOrder, Integer previousIndex);

    /**
     * When remove an entity, we need also to change the index of further records.
     * This function must be called after deletion of entityOrder
     *
     * @param entityOrder removed entity order
     */
    @Modifying
    @Transactional
    @Query("UPDATE EntityOrder e SET e.index = e.index - 1 WHERE e.index > :#{#entityOrder.index}")
    void updateFurtherRecordsWhenDelete(EntityOrder entityOrder);
}
