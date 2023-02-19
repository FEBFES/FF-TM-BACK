package com.febfes.fftmback.domain.common.query;

import com.febfes.fftmback.exception.NoSuitableTypeFilterException;
import com.febfes.fftmback.exception.ValueFilterException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.febfes.fftmback.util.DateUtils.STANDARD_DATE_PATTERN;
import static java.util.Objects.nonNull;

@Slf4j
public record FilterSpecification<T>(
        List<FilterRequest> filterRequests
) implements Specification<T> {

    @Serial
    private static final long serialVersionUID = -2254695601476902813L;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(STANDARD_DATE_PATTERN);

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

    private boolean isValueBelongsToClass(Object value, Class<?> clazz) {
        return value.getClass().isInstance(clazz)
                || clazz.isAssignableFrom(value.getClass());
    }

    private void setFieldTypeToFilter(FilterRequest filter) {
        boolean fieldTypeSet = filter.getOperator().possibleClasses.stream()
                .anyMatch(possibleClass -> {
                    if (possibleClass.equals(Date.class)) {
                        try {
                            LocalDateTime.parse((String) filter.getValue(), FORMATTER);
                            filter.setFieldType(FieldType.DATE);
                            if (nonNull(filter.getValueTo())) {
                                try {
                                    LocalDateTime.parse((String) filter.getValueTo(), FORMATTER);
                                } catch (Exception ignored) {
                                    throw new ValueFilterException(filter.getValue(), filter.getValueTo());
                                }
                            }
                            return true;
                        } catch (Exception ignored) {
                        }
                    }

                    if (nonNull(filter.getValue()) && isValueBelongsToClass(filter.getValue(), possibleClass)) {
                        filter.setFieldType(FieldType.valueOf(possibleClass.getSimpleName().toUpperCase(Locale.ROOT)));

                        if (nonNull(filter.getValueTo()) && !isValueBelongsToClass(filter.getValueTo(), possibleClass)) {
                            throw new ValueFilterException(filter.getValue(), filter.getValueTo());
                        }
                        return true;
                    }

                    if (nonNull(filter.getValues())) {
                        if (isValueBelongsToClass(filter.getValues().get(0), possibleClass)) {
                            boolean allValuesBelongToClass = filter.getValues().stream()
                                    .allMatch(value -> isValueBelongsToClass(value, possibleClass));
                            if (!allValuesBelongToClass) {
                                throw new ValueFilterException(filter.getValues());
                            }
                            filter.setFieldType(FieldType.valueOf(possibleClass.getSimpleName().toUpperCase(Locale.ROOT)));
                            return true;
                        }
                    }

                    return false;
                });

        if (!fieldTypeSet) {
            throw new NoSuitableTypeFilterException(filter.getValue(), filter.getOperator());
        }
    }
}
