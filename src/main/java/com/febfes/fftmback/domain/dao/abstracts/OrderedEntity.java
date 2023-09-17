package com.febfes.fftmback.domain.dao.abstracts;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    public abstract String getColumnToFindOrder();

    public abstract Object getValueToFindOrder();
}
