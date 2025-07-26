package com.fftmback.domain;

import com.febfes.fftmback.domain.abstracts.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "notification")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class NotificationEntity extends BaseEntity {

    public static final String ENTITY_NAME = "Notification";

    @Column(name = "user_id_to")
    private Long userIdTo;

    @Column(name = "message")
    private String message;

    @Column(name = "is_read")
    private boolean isRead = false;
}
