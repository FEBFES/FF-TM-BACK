package com.febfes.fftmback.feign;

import com.febfes.fftmback.config.FeignConfig;
import com.febfes.fftmback.domain.RoleName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "role-client",
        url = "${authentication.url}",
        configuration = FeignConfig.class,
        path = "/v1/roles")
public interface RoleClient {

    @PostMapping("/projects/{projectId}/users/{userId}/change")
    void changeUserRoleOnProject(@PathVariable Long projectId,
                                 @PathVariable Long userId,
                                 @RequestParam RoleName roleName);
}
