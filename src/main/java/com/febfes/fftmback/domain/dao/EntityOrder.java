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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "index")
    Integer index;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "entity_type")
    private EntityType entityType;
}
