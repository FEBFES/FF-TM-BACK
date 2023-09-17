package com.febfes.fftmback.domain.dao;

import com.febfes.fftmback.domain.common.EntityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "entity_order")
public class EntityOrder {

    public static final String ENTITY_NAME = "Entity order";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "index")
    Integer index;		// starts with 1

    @Column(name = "user_id")
    Long userId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Column(name = "entity_id")
    Long entityId;
}
