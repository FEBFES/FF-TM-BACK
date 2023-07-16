package com.febfes.fftmback.domain.dao;

import com.febfes.fftmback.domain.common.TaskPriority;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Table(name = "v_task")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskView extends BaseView {

    @Column(name = "\"name\"")
    private String name;

    @Column(name = "\"description\"")
    private String description;

    @Column(name = "\"projectId\"")
    private Long projectId;

    @Column(name = "\"columnId\"")
    private Long columnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"ownerId\"", referencedColumnName = "id")
    private UserView owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"assigneeId\"", referencedColumnName = "id")
    private UserView assignee;

    @Column(name = "\"priority\"", columnDefinition = "priority")
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"taskTypeId\"", referencedColumnName = "id")
    private TaskTypeEntity taskType;

    @Column(name = "\"filesCounter\"")
    private Long filesCounter;

    @Column(name = "\"updateDate\"")
    private Date updateDate;
}
