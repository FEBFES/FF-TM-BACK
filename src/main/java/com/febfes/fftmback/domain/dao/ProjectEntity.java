package com.febfes.fftmback.domain.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "project")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
public class ProjectEntity extends BaseEntity {

    public static final String ENTITY_NAME = "Project";

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "project_id")
    @ToString.Exclude
    private List<TaskColumnEntity> taskColumnEntityList;
    //TODO problem when project was deleted

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "project_id")
    @ToString.Exclude
    private List<TaskEntity> taskEntityList;
    //TODO problem when project was deleted

    @Column(name = "owner_id")
    private Long ownerId;

    @Transient
    private Boolean isFavourite;

    @ManyToMany
    @ToString.Exclude
    private List<UserEntity> members;
}
