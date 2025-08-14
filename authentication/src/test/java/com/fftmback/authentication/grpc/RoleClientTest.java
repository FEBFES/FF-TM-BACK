package com.fftmback.authentication.grpc;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.grpc.role.RoleRequest;
import com.febfes.fftmback.grpc.role.RoleResponse;
import com.febfes.fftmback.grpc.role.RoleServiceGrpc;
import io.grpc.Server;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration(initializers = RoleClientTest.MockServerInitializer.class)
@ActiveProfiles("test")
class RoleClientTest {

    static Server server;

    @Autowired
    private RoleClient roleClient;

    @AfterEach
    void tearDown() {
        server.shutdownNow();
    }

    @Test
    void getUserRoleNameOnProjectReturnsEnum() {
        RoleName role = roleClient.getUserRoleNameOnProject(1L, 2L);

        assertThat(role).isEqualTo(RoleName.OWNER);
    }

    static class MockServerInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NonNull ConfigurableApplicationContext context) {
            try {
                server = InProcessServerBuilder.forName("test")
                        .directExecutor()
                        .addService(new RoleServiceGrpc.RoleServiceImplBase() {
                            @Override
                            public void getUserRoleNameOnProject(RoleRequest request,
                                                                 StreamObserver<RoleResponse> responseObserver) {
                                responseObserver.onNext(RoleResponse.newBuilder()
                                        .setRoleName(com.febfes.fftmback.grpc.role.RoleName.OWNER)
                                        .build());
                                responseObserver.onCompleted();
                            }
                        })
                        .build()
                        .start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            TestPropertyValues.of(
                    "grpc.client.role-service.address=in-process:test",
                    "grpc.client.role-service.negotiationType=PLAINTEXT"
            ).applyTo(context.getEnvironment());
        }
    }
}
