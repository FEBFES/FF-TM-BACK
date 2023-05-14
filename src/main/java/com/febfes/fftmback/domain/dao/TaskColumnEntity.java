package com.febfes.fftmback.domain.dao;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "task_column")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class TaskColumnEntity extends BaseEntity {

    public static final String ENTITY_NAME = "Task column";

    @Column(name = "name")
    private String name;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "column_order", nullable = false)
    private Integer columnOrder;

    @Column(name = "child_task_column_id")
    private Long childTaskColumnId;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "\"columnId\"")
    @ToString.Exclude
    private List<TaskView> taskList;

}
