package com.febfes.fftmback.domain.dao;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_type")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TaskTypeEntity extends AppEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "project_id", nullable = false)
    private Long projectId;
}
