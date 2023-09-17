package com.febfes.fftmback.service.order;

import com.febfes.fftmback.domain.dao.abstracts.OrderedEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Setter
public class OrderServiceImpl<T extends OrderedEntity> implements OrderService<T> {

    @PersistenceContext
    private EntityManager em;

    private static final String ORDER_FIELD_NAME = "entityOrder";

    @Override
    public Integer getNewOrder(T entity) {
        Query selectMaxOrderQuery = em.createQuery(String.format(
                "SELECT COALESCE((SELECT MAX(e.%s) FROM %s e WHERE e.%s = ?1), 0) + 1",
                ORDER_FIELD_NAME, entity.getClass().getSimpleName(), entity.getColumnToFindOrder()));
        return Integer.parseInt(selectMaxOrderQuery
                .setParameter(1, entity.getValueToFindOrder())
                .getSingleResult().toString());
    }

    @Override
    public void removeEntity(T entity) {
        Query updateFurtherRecordsWhenDeleteQuery = em.createQuery(String.format(
                "UPDATE %1$s e SET e.%2$s = e.%2$s - 1 WHERE e.%2$s > ?1 AND e.%3$s = ?2",
                entity.getClass().getSimpleName(), ORDER_FIELD_NAME, entity.getColumnToFindOrder()));
        updateFurtherRecordsWhenDeleteQuery
                .setParameter(1, entity.getEntityOrder())
                .setParameter(2, entity.getValueToFindOrder())
                .executeUpdate();
    }

    @Override
    public void editOrder(T entity, Integer newOrder) {
        Integer currentOrder = entity.getEntityOrder();
        if (isNull(newOrder) || newOrder.equals(currentOrder)) {
            return;
        }

        Query updateFurtherRecordsQuery = em.createQuery(String.format(
                "UPDATE %1$s e SET e.%2$s = e.%2$s + 1 WHERE e.%2$s >= ?1 AND e.%2$s < ?2 AND e.%3$s = ?3",
                entity.getClass().getSimpleName(), ORDER_FIELD_NAME, entity.getColumnToFindOrder()));
        updateFurtherRecordsQuery
                .setParameter(1, newOrder)
                .setParameter(2, currentOrder)
                .setParameter(3, entity.getValueToFindOrder())
                .executeUpdate();
        entity.setEntityOrder(newOrder);
    }


}
