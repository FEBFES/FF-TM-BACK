package com.febfes.fftmback.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserProjectId implements Serializable {

    @Serial
    private static final long serialVersionUID = -8691264625000961415L;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "project_id")
    private Long projectId;
}
