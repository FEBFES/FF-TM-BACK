package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.RefreshTokenEntity;
import com.febfes.fftmback.dto.auth.RefreshTokenDto;
import com.febfes.fftmback.dto.auth.TokenDto;
import com.febfes.fftmback.dto.auth.UserDetailsDto;
import com.febfes.fftmback.service.RefreshTokenService;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import com.google.gson.Gson;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;

public class AuthenticationControllerTest extends BasicTestClass {

    public static final String PATH_TO_AUTH_API = "/api/v1/auth";
    public static final String USER_USERNAME = "test_username";
    public static final String USER_PASSWORD = "test_password";
    public static final String USER_EMAIL = "test_email@febfes.com";

    @Autowired
    RefreshTokenService refreshTokenService;

    private final Gson gson = new Gson();

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

        Response response = given()
                .contentType(ContentType.JSON)
                .body(userDetailsDto)
                .when()
                .post("%s/authenticate".formatted(PATH_TO_AUTH_API));
        response.then()
                .statusCode(HttpStatus.SC_OK);

        RefreshTokenDto refreshTokenDto = gson.fromJson(response.print(), RefreshTokenDto.class);
        RefreshTokenEntity refreshToken = refreshTokenService.getByToken(refreshTokenDto.refreshToken());
        Assertions.assertNotNull(refreshToken);
    }

    @Test
    void successfulRefreshTokenTest() {
        RefreshTokenDto refreshTokenDto = getRefreshTokenDto();
        TokenDto tokenDto = new TokenDto(refreshTokenDto.refreshToken());
        given()
                .contentType(ContentType.JSON)
                .body(tokenDto)
                .when()
                .post("%s/refresh-token".formatted(PATH_TO_AUTH_API))
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void successfulLogoutTest() {
        RefreshTokenDto refreshTokenDto = getRefreshTokenDto();
        requestWithBearerToken(refreshTokenDto.accessToken())
                .contentType(ContentType.JSON)
                .when()
                .post("%s/logout".formatted(PATH_TO_AUTH_API))
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void successfulHasTokenExpiredTest() {
        RefreshTokenDto refreshTokenDto = getRefreshTokenDto();
        TokenDto tokenDto = new TokenDto(refreshTokenDto.accessToken());
        Response response = given()
                .contentType(ContentType.JSON)
                .body(tokenDto)
                .when()
                .post("%s/has-token-expired".formatted(PATH_TO_AUTH_API));
        Boolean hasTokenExpired = response.then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(Boolean.class);
        Assertions.assertFalse(hasTokenExpired);
    }

    private Response registerUser(UserDetailsDto userDetailsDto) {
        return given()
                .contentType(ContentType.JSON)
                .body(userDetailsDto)
                .when()
                .post("%s/register".formatted(PATH_TO_AUTH_API));
    }

    private RequestSpecification requestWithBearerToken(String token) {
        return given().header("Authorization", "Bearer " + token);
    }

    private RefreshTokenDto getRefreshTokenDto() {
        UserDetailsDto userDetailsDto = new UserDetailsDto(USER_EMAIL, USER_USERNAME, USER_PASSWORD);

        registerUser(userDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_OK);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(userDetailsDto)
                .when()
                .post("%s/authenticate".formatted(PATH_TO_AUTH_API));
        response.then()
                .statusCode(HttpStatus.SC_OK);
        return gson.fromJson(response.print(), RefreshTokenDto.class);
    }
}
