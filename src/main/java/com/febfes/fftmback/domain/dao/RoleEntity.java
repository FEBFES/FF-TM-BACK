package com.febfes.fftmback.domain.dao;

import com.febfes.fftmback.domain.dao.abstracts.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "role")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RoleEntity extends BaseEntity {

    public static final String ENTITY_NAME = "Role";

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}
