package com.febfes.fftmback.domain.projection;

public interface MemberProjection {

    Long getId();

    String getEmail();

    String getUsername();

    String getFirstName();

    String getLastName();

    String getDisplayName();

    String getUserPic();

    String getRoleOnProject();
}
