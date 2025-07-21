package com.fftmback.authentication.integration;

import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.dto.EditUserDto;
import com.fftmback.authentication.dto.UserDto;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.hamcrest.Matchers.equalTo;

class UserControllerTest extends BasicTestClass {

    public static final String PATH_TO_USERS_API = "/api/v1/users";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void successfulGetUserTest() {
        UserEntity user = userService.getUserById(createdUserId);
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{id}".formatted(PATH_TO_USERS_API), createdUserId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("username", equalTo(user.getUsername()));
    }

    @ParameterizedTest
    @MethodSource("updateUserData")
    void successfulUpdateUserTest(EditUserDto editUserDto) {
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(editUserDto)
                .when()
                .put("%s/{id}".formatted(PATH_TO_USERS_API), createdUserId)
                .then()
                .statusCode(HttpStatus.SC_OK);
        UserEntity user = userService.getUserById(createdUserId);
        Assertions.assertEquals(editUserDto.firstName(), user.getFirstName());
        Assertions.assertEquals(editUserDto.lastName(), user.getLastName());
        Assertions.assertEquals(editUserDto.displayName(), user.getDisplayName());
        if (nonNull(editUserDto.password())) {
            Assertions.assertTrue(passwordEncoder.matches(editUserDto.password(), user.getPassword()));
        } else {
            Assertions.assertNotNull(user.getEncryptedPassword());
        }
    }

    static Stream<Arguments> updateUserData() {
        return Stream.of(
                Arguments.of(Instancio.create(EditUserDto.class), 3),
                Arguments.of(Instancio.create(EditUserDto.class), 1)
        );
    }

    @Test
    void successfulGetUsersWithFilterTest() {
        UserEntity user = userService.getUserById(createdUserId);
        List<UserDto> users = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .params("displayName", user.getDisplayName())
                .when()
                .get(PATH_TO_USERS_API)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(new TypeRef<>() {
                });
        Assertions.assertEquals(1, users.size());
    }

}
