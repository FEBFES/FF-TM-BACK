package com.febfes.fftmback.domain.dao;


import com.febfes.fftmback.domain.dao.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @Column(name = "text")
    private String text;
}
