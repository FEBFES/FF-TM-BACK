package com.febfes.fftmback.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "task_column")
@Getter
@Setter
public class TaskColumnEntity extends AppEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "description")
    private String description;

    @Column(name = "column_order", nullable = false)
    private Integer columnOrder;

}
