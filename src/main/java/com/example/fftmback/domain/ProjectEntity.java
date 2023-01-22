package com.example.fftmback.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "projects")
@Getter
@Setter
public class ProjectEntity extends AppEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

}
