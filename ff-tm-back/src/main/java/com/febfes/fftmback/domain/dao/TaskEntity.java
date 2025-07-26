package com.febfes.fftmback.domain.dao;


import com.febfes.fftmback.domain.abstracts.OrderedEntity;
import com.febfes.fftmback.domain.common.TaskPriority;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "task")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class TaskEntity extends OrderedEntity {

    public static final String ENTITY_NAME = "Task";

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

    @Column(name = "assignee_id")
    private Long assigneeId;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    @Type(PostgreSQLEnumType.class)
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_type_id", referencedColumnName = "id")
    @ToString.Exclude
    private TaskTypeEntity taskType;

    @Column(name = "update_date")
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @Column(name = "deadline_date")
    private LocalDateTime deadlineDate;

    @Override
    public String getColumnToFindOrder() {
        return "columnId";
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof TaskEntity that)) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
