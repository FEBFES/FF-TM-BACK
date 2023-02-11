package com.febfes.fftmback.integration;

import com.febfes.fftmback.dto.auth.UserDetailsDto;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class AuthenticationControllerTest extends BasicTestClass {

    public static final String PATH_TO_AUTH_API = "/api/v1/auth";
    public static final String USER_USERNAME = "test_username";
    public static final String USER_PASSWORD = "test_password";
    public static final String USER_EMAIL = "test_email@febfes.com";

    @Test
    void successfulRegisterTest() {
        UserDetailsDto userDetailsDto = new UserDetailsDto(USER_EMAIL, USER_USERNAME, USER_PASSWORD);
        registerUser(userDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void failedRegisterEmailAlreadyExistsTest() {
        UserDetailsDto userDetailsDto = new UserDetailsDto(USER_EMAIL, USER_USERNAME, USER_PASSWORD);
        registerUser(userDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_OK);

        UserDetailsDto wrongEmailUserDetailsDto = new UserDetailsDto(USER_EMAIL, USER_USERNAME + "1", USER_PASSWORD);
        registerUser(wrongEmailUserDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_CONFLICT);
    }

    @Test
    void failedRegisterUsernameAlreadyExistsTest() {
        UserDetailsDto userDetailsDto = new UserDetailsDto(USER_EMAIL, USER_USERNAME, USER_PASSWORD);
        registerUser(userDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_OK);

        UserDetailsDto wrongEmailUserDetailsDto = new UserDetailsDto(USER_EMAIL + "1", USER_USERNAME, USER_PASSWORD);
        registerUser(wrongEmailUserDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_CONFLICT);
    }

    @Test
    void successfulAuthenticateTest() {
        UserDetailsDto userDetailsDto = new UserDetailsDto(USER_EMAIL, USER_USERNAME, USER_PASSWORD);

        registerUser(userDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .contentType(ContentType.JSON)
                .body(userDetailsDto)
                .when()
                .post("%s/authenticate".formatted(PATH_TO_AUTH_API))
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    private Response registerUser(UserDetailsDto userDetailsDto) {
        return given()
                .contentType(ContentType.JSON)
                .body(userDetailsDto)
                .when()
                .post("%s/register".formatted(PATH_TO_AUTH_API));
    }
}
