package com.febfes.fftmback.domain.dao;

import com.febfes.fftmback.domain.abstracts.OrderedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    @Override
    public String getColumnToFindOrder() {
        return "projectId";
    }
}
