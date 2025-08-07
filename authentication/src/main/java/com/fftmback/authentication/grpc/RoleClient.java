package com.fftmback.authentication.grpc;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.grpc.role.RoleRequest;
import com.febfes.fftmback.grpc.role.RoleResponse;
import com.febfes.fftmback.grpc.role.RoleServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class RoleClient {

    @GrpcClient("role-service")
    private RoleServiceGrpc.RoleServiceBlockingStub stub;

    public RoleName getUserRoleNameOnProject(Long projectId, Long userId) {
        RoleRequest request = RoleRequest.newBuilder()
                .setProjectId(projectId)
                .setUserId(userId)
                .build();
        RoleResponse response = stub.getUserRoleNameOnProject(request);
        return RoleName.valueOf(response.getRoleName().name());
    }
}
