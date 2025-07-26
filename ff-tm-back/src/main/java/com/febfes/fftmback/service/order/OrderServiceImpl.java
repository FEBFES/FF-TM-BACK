package com.febfes.fftmback.service.order;

import com.febfes.fftmback.domain.abstracts.OrderedEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Setter
public class OrderServiceImpl<T extends OrderedEntity> implements OrderService<T> {

    @PersistenceContext
    private EntityManager em;

    public static final String ORDER_FIELD_NAME = "entityOrder";

    @Override
    @SuppressWarnings("unchecked")
    public Integer getNewOrder(T entity) {
        // SELECT COALESCE((SELECT MAX(e.%s) FROM %s e WHERE e.%s = ?1), 0) + 1
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<T> root = cq.from((Class<T>) entity.getClass());

        cq.select(cb.coalesce(cb.max(root.get(ORDER_FIELD_NAME)), 0));
        cq.where(cb.equal(root.get(entity.getColumnToFindOrder()), entity.getValueToFindOrder()));

        Integer maxOrder = em.createQuery(cq).getSingleResult();
        return maxOrder + 1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeEntity(T entity) {
        // UPDATE %1$s e SET e.%2$s = e.%2$s - 1 WHERE e.%2$s > ?1 AND e.%3$s = ?2
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate((Class<T>) entity.getClass());
        Root<T> root = update.from((Class<T>) entity.getClass());

        Path<Integer> orderPath = root.get(ORDER_FIELD_NAME);
        Expression<Integer> diff = cb.diff(root.get(ORDER_FIELD_NAME), 1);
        update.set(orderPath, diff);
        update.where(
                cb.and(
                        cb.greaterThan(root.get(ORDER_FIELD_NAME), entity.getEntityOrder()),
                        cb.equal(root.get(entity.getColumnToFindOrder()), entity.getValueToFindOrder())
                )
        );

        em.createQuery(update).executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void editOrder(T entity, Integer newOrder) {
        // UPDATE %1$s e SET e.%2$s = e.%2$s + 1 WHERE e.%2$s >= ?1 AND e.%2$s < ?2 AND e.%3$s = ?3
        Integer currentOrder = entity.getEntityOrder();
        if (newOrder == null || newOrder.equals(currentOrder)) {
            return;
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate((Class<T>) entity.getClass());
        Root<T> root = update.from((Class<T>) entity.getClass());

        Path<Integer> orderPath = root.get(ORDER_FIELD_NAME);
        Expression<Integer> sum = cb.sum(orderPath, 1);
        update.set(orderPath, sum);
        update.where(
                cb.and(
                        cb.greaterThanOrEqualTo(root.get(ORDER_FIELD_NAME), newOrder),
                        cb.lessThan(root.get(ORDER_FIELD_NAME), currentOrder),
                        cb.equal(root.get(entity.getColumnToFindOrder()), entity.getValueToFindOrder())
                )
        );

        em.createQuery(update).executeUpdate();
        entity.setEntityOrder(newOrder);
    }


}
