package com.febfes.fftmback.service.order;

import com.febfes.fftmback.domain.dao.abstracts.OrderedEntity;

import java.util.List;


public interface OrderService<T extends OrderedEntity> {

    /**
     * todo
     *
     * @return
     */
    List<T> sortEntities(List<T> orderedEntitieList);

    /**
     * This function adds entity to the end of array
     */
    void addEntity(T entity, Long userId);

    /**
     *
     */
    void removeEntity(T entity, Long userId);

    /**
     *
     */
    void editIndex(T entity, Integer newIndex, Long userId);
}
