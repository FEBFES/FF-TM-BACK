package com.febfes.fftmback.domain.dao;

import com.febfes.fftmback.domain.common.TaskPriority;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    @Column(name = "\"ownerId\"")
    private Long ownerId;

    @Column(name = "\"priority\"", columnDefinition = "priority")
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"taskTypeId\"", referencedColumnName = "id")
    private TaskTypeEntity taskType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "\"ownerUserPicId\"", referencedColumnName = "user_id")
    private FileEntity ownerUserPic;

    @Column(name = "\"filesCounter\"")
    private Long filesCounter;
}
