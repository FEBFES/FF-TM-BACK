package com.febfes.fftmback.integration;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class RoleControllerTest extends BasicTestClass {

    public static final String PATH_TO_ROLES_API = "/api/v1/roles";

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
}
