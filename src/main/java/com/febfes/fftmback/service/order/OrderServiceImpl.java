package com.febfes.fftmback.service.order;

import com.febfes.fftmback.domain.dao.EntityOrder;
import com.febfes.fftmback.domain.dao.abstracts.OrderedEntity;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.EntityOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl<T extends OrderedEntity> implements OrderService<T> {

    private final EntityOrderRepository entityOrderRepository;

    @Override
    public List<T> sortEntities(List<T> entities) {
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
        return null;
    }

    @Override
    public void addEntity(T entity, Long userId) {
        entityOrderRepository.addTop(userId, entity.getEntityType().name(), entity.getId());
    }

    @Override
    public void removeEntity(T entity, Long userId) {
        EntityOrder entityOrder = getEntityOrder(entity, userId);
        entityOrderRepository.delete(entityOrder);
        entityOrderRepository.updateFurtherRecordsWhenDelete(entityOrder);
    }

    @Override
    public void editIndex(T entity, Integer newIndex, Long userId) {
        EntityOrder entityOrder = getEntityOrder(entity, userId);

        Integer currentIndex = entityOrder.getIndex();
        if (isNull(newIndex) || newIndex.equals(currentIndex)) {
            return;
        }
        entityOrder.setIndex(newIndex);
        entityOrderRepository.save(entityOrder);
        entityOrderRepository.updateFurtherRecords(entityOrder, currentIndex);
    }

    private EntityOrder getEntityOrder(T entity, Long userId) {
        return entityOrderRepository
                .findByEntityIdAndEntityTypeAndUserId(entity.getId(), entity.getEntityType(), userId)
                .orElseThrow(() -> new EntityNotFoundException(EntityOrder.ENTITY_NAME, entity.getId()));
    }


}
