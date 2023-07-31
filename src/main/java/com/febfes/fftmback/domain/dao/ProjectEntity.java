package com.febfes.fftmback.domain.dao;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "project")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true, exclude = {"taskColumnEntityList", "taskEntityList"})
@EqualsAndHashCode(callSuper = true, exclude = {"taskColumnEntityList", "taskEntityList"})
public class ProjectEntity extends BaseEntity {

    public static final String ENTITY_NAME = "Project";

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "project_id")
    private List<TaskColumnEntity> taskColumnEntityList;
    //TODO problem when project was deleted

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "project_id")
    private List<TaskEntity> taskEntityList;
    //TODO problem when project was deleted

    @Column(name = "owner_id")
    private Long ownerId;

    @Transient
    private Boolean isFavourite;
}
