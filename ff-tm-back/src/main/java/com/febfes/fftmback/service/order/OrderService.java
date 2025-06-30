package com.febfes.fftmback.service.order;


import com.febfes.fftmback.domain.abstracts.OrderedEntity;

public interface OrderService<T extends OrderedEntity> {

    /**
     * Gets new entity order for entity. In the end of array.
     *
     * @param entity entity that is needed to get order to.
     * @return new order value.
     */
    Integer getNewOrder(T entity);

    /**
     * When remove an entity, we need also to change the order of further records.
     * This function must be called after deletion of entity
     *
     * @param entity removed entity
     */
    void removeEntity(T entity);

    /**
     * When we change order of entity, we need also to change the order of further records.
     *
     * @param entity   entity to update
     * @param newOrder new order for entity
     */
    void editOrder(T entity, Integer newOrder);
}
