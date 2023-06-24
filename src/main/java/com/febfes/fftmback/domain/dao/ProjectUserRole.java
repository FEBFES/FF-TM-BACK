package com.febfes.fftmback.domain.dao;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "project_user_role")
public class ProjectUserRole {

    @EmbeddedId
    private ProjectUserRoleId id;

    @ManyToOne
    @MapsId("projectId")
    private ProjectEntity project;

    @ManyToOne
    @MapsId("roleId")
    private RoleEntity role;

    @ManyToOne
    @MapsId("userId")
    private UserEntity user;

    @Embeddable
    @Data
    public static class ProjectUserRoleId implements Serializable {

        @Serial
        private static final long serialVersionUID = 79919222128647111L;

        private Long projectId;
        private Long roleId;
        private Long userId;
    }
}
