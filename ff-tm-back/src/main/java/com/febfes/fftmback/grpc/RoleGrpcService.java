package com.febfes.fftmback.grpc;

import com.febfes.fftmback.grpc.role.RoleName;
import com.febfes.fftmback.grpc.role.RoleRequest;
import com.febfes.fftmback.grpc.role.RoleResponse;
import com.febfes.fftmback.grpc.role.RoleServiceGrpc;
import com.febfes.fftmback.service.RoleService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@RequiredArgsConstructor
@GrpcService
public class RoleGrpcService extends RoleServiceGrpc.RoleServiceImplBase {

    private final RoleService roleService;

    @Override
    public void getUserRoleNameOnProject(RoleRequest request,
                                         StreamObserver<RoleResponse> responseObserver) {
        var roleName = roleService.getUserRoleOnProject(request.getProjectId(), request.getUserId()).getName();
        RoleResponse response = RoleResponse.newBuilder()
                .setRoleName(RoleName.valueOf(roleName.name()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
