package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.service.AuthenticationService;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.NonNull;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static com.febfes.fftmback.util.DtoBuilders.PASSWORD;
import static io.restassured.RestAssured.given;

public class RoleControllerTest extends BasicTestClass {

    public static final String PATH_TO_ROLES_API = "/api/v1/roles";

    private Long createdProjectId;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TransactionTemplate txTemplate;

    @BeforeEach
    void beforeEach() {
        createdProjectId = createNewProject();
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
        Long newUserId = createNewUser();
        projectMemberService.addNewMembers(createdProjectId, List.of(newUserId));
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

    @Test
    void failedChangeRoleTest() {
        Long newUserId = createNewUser();
        UserEntity newUser = userService.getUserById(newUserId);
        String newToken = authenticationService.authenticateUser(
                UserEntity.builder().username(newUser.getUsername()).encryptedPassword(PASSWORD).build()
        ).accessToken();
        projectMemberService.addNewMembers(createdProjectId, List.of(newUserId));

        given().header("Authorization", "Bearer " + newToken)
                .contentType(ContentType.JSON)
                .when()
                .post("%s/%s/projects/%d/users/%d/".formatted(PATH_TO_ROLES_API,
                        RoleName.MEMBER_PLUS.name(), createdProjectId, newUserId))
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    private void checkProjectUserRole(RoleName expectedUserRoleName, Long userId) {
        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                UserEntity user = userService.getUserById(userId);
                RoleEntity role = user.getProjectRoles().get(createdProjectId);
                Assertions.assertThat(role.getName())
                        .isEqualTo(expectedUserRoleName);
            }
        });
    }
}
