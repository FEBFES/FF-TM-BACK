package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.EditUserDto;
import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.FileService;
import com.febfes.fftmback.service.UserService;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static com.febfes.fftmback.util.FileUtils.USER_PIC_URN;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class UserControllerTest extends BasicTestClass {

    public static final String PATH_TO_USERS_API = "/api/v1/users";
    private static final String FIRST_NAME = "first";
    private static final String LAST_NAME = "last";
    private static final String DISPLAY_NAME = "display";
    private static final String PASSWORD = "123";

    private String createdUsername;
    private String token;

    @TempDir
    static File tempDir;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private FileService fileService;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("user-pic.folder",
                () -> String.format("%s\\", tempDir.getPath())
        );
    }

    @BeforeEach
    void beforeEach() {
        authenticationService.registerUser(
                UserEntity.builder().email(USER_EMAIL).username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        );
        token = authenticationService.authenticateUser(
                UserEntity.builder().username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        ).accessToken();
        createdUsername = USER_USERNAME;
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
        EditUserDto editUserDto = new EditUserDto(FIRST_NAME, LAST_NAME, DISPLAY_NAME, PASSWORD);
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(editUserDto)
                .when()
                .put("%s/{id}".formatted(PATH_TO_USERS_API), userId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void successfulLoadUserPicTest() {
        Long userId = userService.getUserIdByUsername(createdUsername);
        File imageFile = new File("src/test/resources/image.jpg");
        requestWithBearerToken()
                .multiPart("image", imageFile, "multipart/form-data")
                .when()
                .post("/api/v1/files/user-pic/{userId}", userId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void successfulGetUserPicTest() {
        Long userId = userService.getUserIdByUsername(createdUsername);
        MultipartFile file = new MockMultipartFile("image.jpg", "image", "jpg", new byte[]{1});
        fileService.saveFile(userId, userId, EntityType.USER_PIC, file);
        requestWithBearerToken()
                .when()
                .get("/api/v1/files/user-pic/{userId}", userId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void successfulGetUsersWithFilterTest() {
        List<UserDto> users = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .params("filter", "[{\"property\":\"displayName\",\"operator\":\"LIKE\",\"value\":\"user\"}]")
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

    @Test
    void getUsersWithNullFilterTest() {
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get(PATH_TO_USERS_API)
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void successfulGetUserPicFromUserInfoTest() {
        successfulLoadUserPicTest();
        Long userId = userService.getUserIdByUsername(createdUsername);
        UserDto userDto = requestWithBearerToken()
                .when()
                .get("%s/{id}".formatted(PATH_TO_USERS_API), userId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(UserDto.class);
        Assertions.assertNotNull(userDto.userPic());
        Assertions.assertEquals(String.format(USER_PIC_URN, userId), userDto.userPic());
    }

    @Test
    void successfulDeleteUserPicTest() {
        successfulLoadUserPicTest();
        Long userId = userService.getUserIdByUsername(createdUsername);
        requestWithBearerToken()
                .when()
                .delete("/api/v1/files/user-pic/{userId}", userId)
                .then()
                .statusCode(HttpStatus.SC_OK);
        String userPicUrn = String.format(USER_PIC_URN, userId);
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> fileService.getFile(userPicUrn)
        );
    }


    private RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }

}
