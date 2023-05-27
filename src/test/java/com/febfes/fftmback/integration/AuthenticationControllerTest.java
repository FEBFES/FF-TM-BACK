package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.dao.RefreshTokenEntity;
import com.febfes.fftmback.dto.auth.AccessTokenDto;
import com.febfes.fftmback.dto.auth.RefreshTokenDto;
import com.febfes.fftmback.dto.auth.TokenDto;
import com.febfes.fftmback.dto.auth.UserDetailsDto;
import com.febfes.fftmback.service.RefreshTokenService;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
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
        TokenDto refreshTokenDto = response.then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(TokenDto.class);

        RefreshTokenEntity refreshToken = refreshTokenService.getByToken(refreshTokenDto.refreshToken());
        Assertions.assertNotNull(refreshToken);
    }

    @Test
    void successfulRefreshTokenTest() {
        TokenDto refreshTokenDto = getRefreshTokenDto();
        RefreshTokenDto tokenDto = new RefreshTokenDto(refreshTokenDto.refreshToken());
        given()
                .contentType(ContentType.JSON)
                .body(tokenDto)
                .when()
                .post("%s/refresh-token".formatted(PATH_TO_AUTH_API))
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void successfulCheckTokenExpirationTest() {
        TokenDto refreshTokenDto = getRefreshTokenDto();
        AccessTokenDto accessTokenDto = new AccessTokenDto(refreshTokenDto.accessToken());
        given()
                .contentType(ContentType.JSON)
                .body(accessTokenDto)
                .when()
                .post("%s/check-token-expiration".formatted(PATH_TO_AUTH_API))
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

    private TokenDto getRefreshTokenDto() {
        UserDetailsDto userDetailsDto = new UserDetailsDto(USER_EMAIL, USER_USERNAME, USER_PASSWORD);

        registerUser(userDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_OK);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(userDetailsDto)
                .when()
                .post("%s/authenticate".formatted(PATH_TO_AUTH_API));
        return response.then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(TokenDto.class);
    }
}
