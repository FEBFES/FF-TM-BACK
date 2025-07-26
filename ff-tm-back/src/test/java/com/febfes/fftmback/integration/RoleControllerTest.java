package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.integration.basic.BasicTestClass;
import com.febfes.fftmback.service.RoleService;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static io.restassured.RestAssured.given;

public class RoleControllerTest extends BasicTestClass {

    public static final String PATH_TO_ROLES_API = "/api/v1/roles";

    @Autowired
    private RoleService roleService;

    @Value("${custom-headers.user-role}")
    private String userRoleHeader;

    private Long createdProjectId;

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
    @Disabled("Testing that the role matches the one required to execute the method should occur in the authentication module")
    void failedChangeRoleTest() {
        Long newUserId = createNewUser();
        String newToken = generateToken(newUserId, username + "2");
        projectMemberService.addNewMembers(createdProjectId, List.of(newUserId));

        given().header("Authorization", "Bearer " + newToken)
                .header(userRoleHeader, RoleName.MEMBER)
                .contentType(ContentType.JSON)
                .when()
                .post("%s/%s/projects/%d/users/%d/".formatted(PATH_TO_ROLES_API,
                        RoleName.MEMBER_PLUS.name(), createdProjectId, newUserId))
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    private void checkProjectUserRole(RoleName expectedUserRoleName, Long userId) {
        var roleEntity = roleService.getUserRoleOnProject(createdProjectId, userId);
        Assertions.assertThat(roleEntity.getName())
                .isEqualTo(expectedUserRoleName);
    }
}
