package com.febfes.fftmback.domain.dao.OrderedEntity;

import com.febfes.fftmback.domain.dao.BaseEntity;
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

    @Column(name = "child_entity_id")
    Long childEntityId;

    @Column(name = "entity_order")
    Integer entityOrder;

    @Column(name = "group_id")
    String parentEntityId;
}
