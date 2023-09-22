package com.febfes.fftmback.domain.dao;

import com.febfes.fftmback.domain.dao.abstracts.OrderedEntity;
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
public class TaskColumnEntity extends OrderedEntity {

    public static final String ENTITY_NAME = "Task column";

    @Column(name = "name")
    private String name;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "\"columnId\"")
    @ToString.Exclude
    private List<TaskView> taskList;

    @Override
    public String getColumnToFindOrder() {
        return "projectId";
    }
}
