package com.febfes.fftmback.domain.dao;

import com.febfes.fftmback.domain.abstracts.OrderedView;
import com.febfes.fftmback.domain.common.TaskPriority;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "v_task")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class TaskView extends OrderedView {

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

    @Column(name = "\"assigneeId\"")
    private Long assigneeId;

    @Column(name = "\"priority\"", columnDefinition = "priority")
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"taskTypeId\"", referencedColumnName = "id")
    @ToString.Exclude
    private TaskTypeEntity taskType;

    @Column(name = "\"filesCounter\"")
    private Long filesCounter;

    @Column(name = "\"updateDate\"")
    private LocalDateTime updateDate;

    @Column(name = "\"deadlineDate\"")
    private LocalDateTime deadlineDate;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof TaskView taskView)) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        return getId() != null && Objects.equals(getId(), taskView.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
