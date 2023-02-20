package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.EditUserDto;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.UserService;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class UserControllerTest extends BasicTestClass {

    public static final String PATH_TO_USERS_API = "/api/v1/users";
    private static final String FIRST_NAME = "first";
    private static final String LAST_NAME = "last";
    private static final String PASSWORD = "123";

    private String createdUsername;
    private String token;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @BeforeEach
    void beforeEach() {
        token = authenticationService.registerUser(
                UserEntity.builder().email(USER_EMAIL).username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        ).token();
        createdUsername = userService.loadUserByUsername(USER_USERNAME).getUsername();
    }

    @Test
    void successfulGetUserTest() {
        Long userId = userService.getUserIdByUsername(createdUsername);
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{id}".formatted(PATH_TO_USERS_API), userId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("username", equalTo(createdUsername));
    }

    @Test
    void successfulUpdateUserTest() {
        Long userId = userService.getUserIdByUsername(createdUsername);
        EditUserDto editUserDto = new EditUserDto(FIRST_NAME, LAST_NAME, PASSWORD);

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(editUserDto)
                .when()
                .put("%s/{id}".formatted(PATH_TO_USERS_API), userId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    private RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }

}
