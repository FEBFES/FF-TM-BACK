package com.febfes.fftmback.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "project")
@Getter
@Setter
public class ProjectEntity extends AppEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "project_id")
    private List<TaskColumnEntity> taskColumnEntityList;
    //TODO problem n+1 when project was deleted
}
