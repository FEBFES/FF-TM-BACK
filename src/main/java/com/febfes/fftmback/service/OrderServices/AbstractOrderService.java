//package com.febfes.fftmback.service.impl;
//
//import com.febfes.fftmback.domain.dao.abstracts.OrderedEntity;
//import com.febfes.fftmback.service.OrderService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class OrderServiceImpl <T extends OrderedEntity, R extends JpaRepository<T, Long>> implements OrderService<T, R> {
//
//    private final R repository;
//
//    @Override
//    public List<T> sortEntities(List<T> orderedEntitieList) {
//        Map<Long, T> childIdToOrderedEntity = orderedEntitieList.stream()
//                .collect(Collectors.toMap(OrderedEntity::getChildEntityId, Function.identity()));
//        List<T> result = new ArrayList<>();
//        // last element has childEntityId == null
//        Long currentEntityId = null;
//        // todo
//        for (int i = childIdToOrderedEntity.size(); i > 0; i--) {
//            T entity = childIdToOrderedEntity.get(currentEntityId);
//            entity.setEntityOrder(i);
//            result.add(entity);
//            currentEntityId = entity.getId();
//        }
//        Collections.reverse(result);
//        return result;
//    }
//
//    @Override
//    public void addEntity(T entity) {
//
//    }
//
//
//}
