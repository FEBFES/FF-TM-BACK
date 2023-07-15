package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.specification.ColumnWithTasksSpec;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

    @PersistenceContext
    private EntityManager em;

    public List<TaskColumnEntity> getColumns(ColumnWithTasksSpec spec, Long projectId) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<TaskColumnEntity> cq = builder.createQuery(TaskColumnEntity.class);
        Root<TaskColumnEntity> rootColumn = cq.from(TaskColumnEntity.class);
//        Predicate criteria = builder.conjunction();
        Join<TaskColumnEntity, TaskView> columnsWithTasks = rootColumn.join("taskList", JoinType.LEFT);
//        criteria = builder.and(criteria, builder.like(columnsWithTasks.get("name"), "Task"));

//        CriteriaQuery<TaskColumnEntity> newQuery = builder.createQuery(TaskColumnEntity.class);
//        Root<TaskColumnEntity> newRootColumn = newQuery.from(TaskColumnEntity.class);
//        Subquery<TaskColumnEntity> subquery = cq.subquery(TaskColumnEntity.class);
//        JpaRoot<TaskColumnEntity> subRoot = cq.from(subquery);
//        for (Expression<Boolean> e : Objects.requireNonNull(spec.toPredicate(
//                newRootColumn, newQuery, builder)
//        ).getExpressions()) {
//            criteria = builder.and(criteria, e);
//        }
//        SqmSubQuery<Tuple> subQuery = (SqmSubQuery<Tuple>) cq.subquery(Tuple.class);
        Predicate criteria = spec.toPredicate(rootColumn, cq, builder);
        columnsWithTasks.on(criteria);

        cq.select(rootColumn).where(builder.equal(rootColumn.get("projectId"), projectId));

        return em.createQuery(cq).getResultList();
    }
}
