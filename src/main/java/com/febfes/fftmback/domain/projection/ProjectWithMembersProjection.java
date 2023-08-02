package com.febfes.fftmback.domain.projection;

import java.util.Date;

public interface ProjectWithMembersProjection {

    Long getId();

    String getName();

    String getDescription();

    Date getCreateDate();

    Long getOwnerId();

    Boolean getIsFavourite();

//    List<MemberProjection> getMembers();

    String getRoleName();

    String getRoleDescription();
}
