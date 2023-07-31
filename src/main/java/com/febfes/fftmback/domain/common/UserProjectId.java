package com.febfes.fftmback.domain.common;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProjectId implements Serializable {

    @Serial
    private static final long serialVersionUID = -8691264625000961415L;

    private Long userId;
    private Long projectId;
}
