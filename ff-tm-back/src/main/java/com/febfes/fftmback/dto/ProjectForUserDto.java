package com.febfes.fftmback.dto;

import com.febfes.fftmback.domain.projection.ProjectForUserProjection;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ProjectForUserDto implements Serializable {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createDate;
    private Long ownerId;
    private Boolean isFavourite;
    private String roleName;
    private String roleDescription;

    @Serial
    private static final long serialVersionUID = 6434428853016698293L;

    public ProjectForUserDto(ProjectForUserProjection p) {
        this.id = p.getId();
        this.name = p.getName();
        this.description = p.getDescription();
        this.createDate = p.getCreateDate();
        this.ownerId = p.getOwnerId();
        this.isFavourite = p.getIsFavourite();
        this.roleName = p.getRoleName();
        this.roleDescription = p.getRoleDescription();
    }
}
