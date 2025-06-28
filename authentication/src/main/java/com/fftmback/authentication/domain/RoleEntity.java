package com.fftmback.authentication.domain;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "role")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RoleEntity extends BaseEntity {

    public static final String ENTITY_NAME = "Role";

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Column(name = "description")
    private String description;
}
