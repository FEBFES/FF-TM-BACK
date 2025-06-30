package com.febfes.fftmback.domain.dao;


import com.febfes.fftmback.domain.abstracts.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "text")
    private String text;
}
