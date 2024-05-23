package com.febfes.fftmback.domain.projection;

import java.time.LocalDateTime;

public interface ProjectWithMembersProjection {

    Long getId();

    String getName();

    String getDescription();

    LocalDateTime getCreateDate();

    Long getOwnerId();

    Boolean getIsFavourite();

    String getRoleName();

    String getRoleDescription();
}
