package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.dto.DashboardDto;

public interface DashboardService {

    DashboardDto getDashboard(Long id, TaskSpec taskSpec);
}
