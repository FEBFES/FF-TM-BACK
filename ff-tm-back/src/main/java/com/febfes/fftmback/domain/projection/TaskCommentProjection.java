package com.febfes.fftmback.domain.projection;

import java.time.LocalDateTime;

public interface TaskCommentProjection {

    Long getId();

    LocalDateTime getCreateDate();

    Long getCreatorId();

    Long getTaskId();

    String getText();

    String getCreatorName();
}
