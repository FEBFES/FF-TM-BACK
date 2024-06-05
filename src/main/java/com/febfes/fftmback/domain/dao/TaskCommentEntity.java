package com.febfes.fftmback.domain.dao;


import com.febfes.fftmback.domain.dao.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "task_comment")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskCommentEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private UserEntity creator;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "text", length = 1000)
    private String text;
}
