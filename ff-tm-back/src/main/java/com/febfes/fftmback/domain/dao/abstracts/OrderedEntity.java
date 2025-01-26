package com.febfes.fftmback.domain.dao.abstracts;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Field;

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public abstract class OrderedEntity extends BaseEntity {

    @Column(name = "entity_order")
    private Integer entityOrder;

    /**
     * This method is used in OrderService. Field name that returned in this method will be used to
     * find entity order
     *
     * @return field name
     */
    public abstract String getColumnToFindOrder();

    public Object getValueToFindOrder() {
        try {
            Field field = getClass().getDeclaredField(getColumnToFindOrder());
            field.setAccessible(true);
            return field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
