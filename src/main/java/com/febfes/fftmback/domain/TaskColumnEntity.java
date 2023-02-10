package com.febfes.fftmback.domain;

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
public class TaskColumnEntity extends AppEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "column_order", nullable = false)
    private Integer columnOrder;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "column_id")
    @ToString.Exclude
    private List<TaskEntity> taskEntityList;
    //TODO problem when project was deleted

}
