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
import static java.util.Objects.isNull;
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

    public static void setFieldTypeToFilter(FilterRequest filter) {
        boolean fieldTypeSet = filter.getOperator().possibleClasses.stream()
                .anyMatch(possibleClass -> {
                    if (possibleClass.equals(Date.class) && isValueADate(filter)) {
                        filter.setFieldType(FieldType.DATE);
                        return true;
                    }

                    if (isValueBelongsToClass(filter.getValue(), possibleClass)) {
                        filter.setFieldType(FieldType.valueOf(possibleClass.getSimpleName().toUpperCase(Locale.ROOT)));

                        if (isValueNotBelongsToClass(filter.getValueTo(), possibleClass)) {
                            throw new ValueFilterException(filter.getValue(), filter.getValueTo());
                        }
                        return true;
                    }

                    if (doAllValuesBelongToClass(filter.getValues(), possibleClass)) {
                        filter.setFieldType(FieldType.valueOf(possibleClass.getSimpleName().toUpperCase(Locale.ROOT)));
                        return true;
                    }

                    return false;
                });

        if (!fieldTypeSet) {
            throw new NoSuitableTypeFilterException(filter.getValue(), filter.getOperator());
        }
    }

    private static boolean canParseToDate(Object value) {
        try {
            LocalDateTime.parse((String) value, FORMATTER);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    private static boolean isValueADate(FilterRequest filter) {
        if (canParseToDate(filter.getValue())) {
            if (nonNull(filter.getValueTo())) {
                if (canParseToDate(filter.getValueTo())) {
                    return true;
                }
                throw new ValueFilterException(filter.getValue(), filter.getValueTo());
            }
            return true;
        }

        return false;
    }

    private static boolean isValueBelongsToClass(Object value, Class<?> clazz) {
        return nonNull(value) && (value.getClass().isInstance(clazz)
                || clazz.isAssignableFrom(value.getClass()));
    }

    private static boolean isValueNotBelongsToClass(Object value, Class<?> clazz) {
        return nonNull(value) && !(value.getClass().isInstance(clazz)
                || clazz.isAssignableFrom(value.getClass()));
    }

    private static boolean doAllValuesBelongToClass(List<Object> values, Class<?> clazz) {
        if (isNull(values)) {
            return false;
        }

        if (isValueBelongsToClass(values.get(0), clazz)) {
            boolean allValuesBelongToClass = values.stream()
                    .allMatch(value -> isValueBelongsToClass(value, clazz));
            if (!allValuesBelongToClass) {
                throw new ValueFilterException(values);
            }
            return true;
        }
        return false;
    }
}
