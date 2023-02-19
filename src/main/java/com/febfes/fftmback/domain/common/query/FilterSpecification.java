package com.febfes.fftmback.domain.common.query;

import com.febfes.fftmback.exception.NoSuitableTypeFilterException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.util.List;
import java.util.Locale;

import static java.util.Objects.isNull;

@Slf4j
public record FilterSpecification<T>(
        List<FilterRequest> filterRequests
) implements Specification<T> {

    @Serial
    private static final long serialVersionUID = -2254695601476902813L;

    @Override
    public Predicate toPredicate(
            @NonNull Root<T> root,
            @NonNull CriteriaQuery<?> query,
            @NonNull CriteriaBuilder cb
    ) {
        Predicate predicate = cb.equal(cb.literal(Boolean.TRUE), Boolean.TRUE);

        for (FilterRequest filter : filterRequests) {
            log.info("Filter: {} {} {}", filter.getProperty(), filter.getOperator().toString(), filter.getValue());
            setFieldTypeToFilter(filter);
            predicate = filter.getOperator().build(root, cb, filter, predicate);
        }

        return predicate;
    }

    private void setFieldTypeToFilter(FilterRequest filter) {
        filter.getOperator().possibleClasses.forEach(possibleClass -> {
            if (filter.getValue().getClass().isInstance(possibleClass)
                    || possibleClass.isAssignableFrom(filter.getValue().getClass())) {

                filter.setFieldType(FieldType.valueOf(possibleClass.getSimpleName().toUpperCase(Locale.ROOT)));
            }
        });
        if (isNull(filter.getFieldType())) {
            throw new NoSuitableTypeFilterException(filter.getValue(), filter.getOperator());
        }
    }
}
