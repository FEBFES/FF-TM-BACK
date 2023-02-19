package com.febfes.fftmback.domain.common.query;

import com.febfes.fftmback.util.DateUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
public enum Operator {

    EQUAL(List.of(Boolean.class, Date.class, Number.class, String.class)) {
        /*
        Example: SELECT * FROM table WHERE field = ?
         */
        public <T> Predicate build(
                Root<T> root,
                CriteriaBuilder cb,
                FilterRequest request,
                Predicate predicate
        ) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            Expression<?> property = root.get(request.getProperty());
            return cb.and(cb.equal(property, value), predicate);
        }
    },

    NOT_EQUAL(List.of(Boolean.class, Date.class, Number.class, String.class)) {
        /*
        Example: SELECT * FROM table WHERE field != ?
         */
        public <T> Predicate build(
                Root<T> root,
                CriteriaBuilder cb,
                FilterRequest request,
                Predicate predicate
        ) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            Expression<?> property = root.get(request.getProperty());
            return cb.and(cb.notEqual(property, value), predicate);
        }
    },

    LIKE(List.of(String.class)) {
        /*
        Example: SELECT * FROM table WHERE field LIKE '%?%'
         */
        public <T> Predicate build(
                Root<T> root,
                CriteriaBuilder cb,
                FilterRequest request,
                Predicate predicate
        ) {
            Expression<String> property = root.get(request.getProperty());
            return cb.and(
                    cb.like(
                            cb.upper(property),
                            MessageFormat.format("%{0}%", request.getValue().toString().toUpperCase())
                    ),
                    predicate
            );
        }
    },

    IN(List.of(Number.class, String.class)) {
        /*
        Example: SELECT * FROM table WHERE field IN (?)
         */
        public <T> Predicate build(
                Root<T> root,
                CriteriaBuilder cb,
                FilterRequest request,
                Predicate predicate
        ) {
            List<Object> values = request.getValues();
            CriteriaBuilder.In<Object> inClause = cb.in(root.get(request.getProperty()));
            for (Object value : values) {
                inClause.value(request.getFieldType().parse(value.toString()));
            }
            return cb.and(inClause, predicate);
        }
    },

    BETWEEN(List.of(Date.class, Number.class)) {
        /*
        Example: SELECT * FROM table WHERE field >= ? AND field <= ?
         */
        public <T> Predicate build(
                Root<T> root,
                CriteriaBuilder cb,
                FilterRequest request,
                Predicate predicate
        ) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            Object valueTo = request.getFieldType().parse(request.getValueTo().toString());
            if (request.getFieldType().equals(FieldType.DATE)) {
                Date startDate = DateUtils.convertLocalDateTimeToDate((LocalDateTime) value);
                Date endDate = DateUtils.convertLocalDateTimeToDate((LocalDateTime) valueTo);
                Expression<Date> property = root.get(request.getProperty());
                return cb.and(
                        cb.and(
                                cb.greaterThanOrEqualTo(property, startDate),
                                cb.lessThanOrEqualTo(property, endDate)
                        ),
                        predicate
                );
            }

            Number start = (Number) value;
            Number end = (Number) valueTo;
            Expression<Number> property = root.get(request.getProperty());
            return cb.and(cb.and(cb.ge(property, start), cb.le(property, end)), predicate);
        }
    };

    public abstract <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate);

    public final List<Class<?>> possibleClasses;

    Operator(List<Class<?>> possibleClasses) {
        this.possibleClasses = possibleClasses;
    }
}
