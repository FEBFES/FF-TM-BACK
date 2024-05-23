package com.febfes.fftmback.domain.projection;

import java.time.LocalDateTime;

public interface ProjectForUserProjection {

    Long getId();

    String getName();

    String getDescription();

    LocalDateTime getCreateDate();

    Long getOwnerId();

    Boolean getIsFavourite();

    String getRoleName();

    String getRoleDescription();
}
