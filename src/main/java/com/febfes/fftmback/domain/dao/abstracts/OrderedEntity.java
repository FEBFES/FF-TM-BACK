package com.febfes.fftmback.domain.dao.abstracts;

import com.febfes.fftmback.domain.common.EntityType;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@ToString
public abstract class OrderedEntity extends BaseEntity {

    public abstract EntityType getEntityType();
}
