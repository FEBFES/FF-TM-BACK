package com.febfes.fftmback.domain.projection;

import java.util.Date;

public interface ColumnProjection extends OrderProjection {

    Long getId();

    String getName();

    Date getCreateDate();
}
