package com.fftmback.authentication.integration;

import com.fftmback.authentication.dto.*;
import com.fftmback.authentication.service.RefreshTokenCacheService;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.instancio.Select.field;

class AuthenticationControllerTest extends BasicTestClass {

    public static final String PATH_TO_AUTH_API = "/api/v1/auth";
    public static final String EMAIL_PATTERN = "#a#a#a#a#a#a@example.com";

    @Autowired
    RefreshTokenCacheService refreshTokenCacheService;

    @Test
    void successfulRegisterTest() {
        UserDetailsDto userDetailsDto = Instancio.of(UserDetailsDto.class)
                .generate(field(UserDetailsDto::email), gen -> gen.text().pattern(EMAIL_PATTERN))
                .create();
        registerUser(userDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void failedRegisterEmailAlreadyExistsTest() {
        UserDetailsDto userDetailsDto = Instancio.of(UserDetailsDto.class)
                .generate(field(UserDetailsDto::email), gen -> gen.text().pattern(EMAIL_PATTERN))
                .create();
        registerUser(userDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_OK);

        UserDetailsDto wrongEmailUserDetailsDto = Instancio.of(UserDetailsDto.class)
                .set(field(UserDetailsDto::email), userDetailsDto.email())
                .create();
        registerUser(wrongEmailUserDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_CONFLICT);
    }

    @Test
    void failedRegisterUsernameAlreadyExistsTest() {
        UserDetailsDto userDetailsDto = Instancio.of(UserDetailsDto.class)
                .generate(field(UserDetailsDto::email), gen -> gen.text().pattern(EMAIL_PATTERN))
                .create();
        registerUser(userDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_OK);

        UserDetailsDto wrongEmailUserDetailsDto = Instancio.of(UserDetailsDto.class)
                .set(field(UserDetailsDto::username), userDetailsDto.username())
                .create();
        registerUser(wrongEmailUserDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    void successfulAuthenticateTest() {
        UserDetailsDto userDetailsDto = Instancio.of(UserDetailsDto.class)
                .generate(field(UserDetailsDto::email), gen -> gen.text().pattern(EMAIL_PATTERN))
                .create();

        registerUser(userDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_OK);

        var dto = new AuthenticationDto(userDetailsDto.username(), userDetailsDto.password());
        Response response = given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("%s/authenticate".formatted(PATH_TO_AUTH_API));
        GetAuthDto authDto = response.then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(GetAuthDto.class);

        RefreshTokenDto refreshToken = refreshTokenCacheService.getByToken(authDto.refreshToken());
        Assertions.assertNotNull(refreshToken);
    }

    @Test
    void successfulRefreshTokenTest() {
        GetAuthDto authDto = getRefreshTokenDto();
        RefreshOnlyTokenDto tokenDto = new RefreshOnlyTokenDto(authDto.refreshToken());
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
        GetAuthDto authDto = getRefreshTokenDto();
        AccessTokenDto accessTokenDto = new AccessTokenDto(authDto.accessToken());
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

    private GetAuthDto getRefreshTokenDto() {
        UserDetailsDto userDetailsDto = Instancio.of(UserDetailsDto.class)
                .generate(field(UserDetailsDto::email), gen -> gen.text().pattern(EMAIL_PATTERN))
                .create();

        registerUser(userDetailsDto)
                .then()
                .statusCode(HttpStatus.SC_OK);

        var dto = new AuthenticationDto(userDetailsDto.username(), userDetailsDto.password());
        Response response = given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("%s/authenticate".formatted(PATH_TO_AUTH_API));
        return response.then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(GetAuthDto.class);
    }
}
