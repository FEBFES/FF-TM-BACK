package com.febfes.fftmback.domain.projection;

import java.util.Date;

public interface ProjectWithMembersProjection {

    Long getId();

    String getName();

    String getDescription();

    Date getCreateDate();

    Long getOwnerId();

    Boolean getIsFavourite();

    String getRoleName();

    String getRoleDescription();
}
