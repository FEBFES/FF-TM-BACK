package com.febfes.fftmback.domain.dao;


import com.febfes.fftmback.domain.common.TaskPriority;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;


@Entity
@Table(name = "task")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskEntity extends AppEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "column_id")
    private Long columnId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "priority")
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_type_id", referencedColumnName = "id")
    private TaskTypeEntity taskType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id", insertable=false, updatable=false)
    private UserPicEntity ownerUserPic;

    @Column(name = "files_counter")
    @Formula(value="(SELECT count(*) FROM task_file tf WHERE tf.task_id = this_.id)")
    private Long filesCounter;
}
