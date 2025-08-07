package com.febfes.fftmback.grpc;

import com.febfes.fftmback.grpc.role.RoleName;
import com.febfes.fftmback.grpc.role.RoleRequest;
import com.febfes.fftmback.grpc.role.RoleResponse;
import com.febfes.fftmback.grpc.role.RoleServiceGrpc;
import com.febfes.fftmback.integration.basic.BasicTestClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class RoleGrpcServiceTest extends BasicTestClass {

    @Value("${grpc.server.port}")
    int grpcPort;

    private ManagedChannel channel;
    private RoleServiceGrpc.RoleServiceBlockingStub stub;

    @BeforeEach
    void setUpChannel() {
        channel = ManagedChannelBuilder.forAddress("localhost", grpcPort)
                .usePlaintext()
                .build();
        stub = RoleServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void shutdown() {
        channel.shutdownNow();
    }

    @Test
    void getUserRoleNameOnProject() {
        Long projectId = createNewProject();

        RoleRequest request = RoleRequest.newBuilder()
                .setProjectId(projectId)
                .setUserId(createdUserId)
                .build();
        RoleResponse response = stub.getUserRoleNameOnProject(request);

        Assertions.assertThat(response.getRoleName())
                .isEqualTo(RoleName.OWNER);
    }
}
