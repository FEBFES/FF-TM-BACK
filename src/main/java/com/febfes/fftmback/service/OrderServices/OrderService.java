package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.OrderedEntities.OrderedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderService <T extends OrderedEntity, R extends JpaRepository<T, Long>> {

    /**
     * todo
     * @return
     */
    List<T> sortEntities(List<T> orderedEntitieList);

    /**
     *
     */
    void addEntity(T entity);
}
