package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.service.UserService;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.NonNull;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static com.febfes.fftmback.integration.ProjectControllerTest.PROJECT_NAME;
import static io.restassured.RestAssured.given;

public class RoleControllerTest extends BasicTestClass {

    public static final String PATH_TO_ROLES_API = "/api/v1/roles";

    private String token;
    private Long createdProjectId;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate txTemplate;

    @BeforeEach
    void beforeEach() {
        authenticationService.registerUser(
                UserEntity.builder().email(USER_EMAIL).username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        );
        token = authenticationService.authenticateUser(
                UserEntity.builder().username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        ).accessToken();

        ProjectEntity projectEntity = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME).build(),
                USER_USERNAME
        );
        createdProjectId = projectEntity.getId();
    }

    @Test
    void successfulGetRolesTest() {
        Response response = given().contentType(ContentType.JSON)
                .when()
                .get(PATH_TO_ROLES_API);
        response.then()
                .statusCode(HttpStatus.SC_OK);

        int size = response
                .jsonPath()
                .getInt("data.size()");
        Assertions.assertThat(size)
                .isEqualTo(3);
    }

    @Test
    void successfulChangeRoleTest() {
        authenticationService.registerUser(
                UserEntity.builder().email(USER_EMAIL + "1").username(USER_USERNAME + "1").encryptedPassword(USER_PASSWORD).build()
        );
        Long newUserId = userService.getUserIdByUsername(USER_USERNAME + "1");
        projectService.addNewMembers(createdProjectId, List.of(newUserId));
        checkProjectUserRole(RoleName.MEMBER, newUserId);

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .post("%s/%s/projects/%d/users/%d/".formatted(PATH_TO_ROLES_API,
                        RoleName.MEMBER_PLUS.name(), createdProjectId, newUserId))
                .then()
                .statusCode(HttpStatus.SC_OK);

        checkProjectUserRole(RoleName.MEMBER_PLUS, newUserId);
    }

    private RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }

    private void checkProjectUserRole(RoleName expectedUserRoleName, Long userId) {
        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                UserEntity user = userService.getUserById(userId);
                ProjectEntity project = projectService.getProject(createdProjectId);
                RoleEntity role = user.getProjectRoles().get(project);
                Assertions.assertThat(role.getName())
                        .isEqualTo(expectedUserRoleName.name());
            }
        });
    }
}
