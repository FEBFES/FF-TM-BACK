package com.fftmback.authentication.feign;

import com.febfes.fftmback.domain.RoleName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "role-client",
        url = "${ff-tm-back.url}",
        path = "/v1/roles")
public interface RoleClient {

    // TODO: write tests for this client
    @GetMapping("/projects/{projectId}/users/{userId}/")
    RoleName getUserRoleNameOnProject(@PathVariable Long projectId,
                                      @PathVariable Long userId);
}
