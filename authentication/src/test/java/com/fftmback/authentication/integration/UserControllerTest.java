package com.fftmback.authentication.integration;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.hamcrest.Matchers.equalTo;

class UserControllerTest extends BasicTestClass {

    public static final String PATH_TO_USERS_API = "/api/v1/users";

    @TempDir
    static File tempDir;

    @Autowired
    private FileService fileService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("user-pic.folder",
                () -> String.format("%s\\", tempDir.getPath())
        );
    }

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
    void successfulLoadUserPicTest() {
        UserPicDto userPicDto = loadUserPic();
        Assertions.assertEquals("/files/user-pic/%d".formatted(createdUserId), userPicDto.fileUrn());
        Assertions.assertEquals(createdUserId, userPicDto.userId());
    }

    @Test
    void successfulGetUserPicTest() throws IOException {
        MultipartFile file = new MockMultipartFile("image.jpg", "image", "jpg", new byte[]{1});
        fileService.saveFile(createdUserId, createdUserId, EntityType.USER_PIC, file);
        requestWithBearerToken()
                .when()
                .get("/api/v1/files/user-pic/{userId}", createdUserId)
                .then()
                .statusCode(HttpStatus.SC_OK);
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

    @Test
    void successfulGetUserPicFromUserInfoTest() {
        loadUserPic();
        UserDto userDto = requestWithBearerToken()
                .when()
                .get("%s/{id}".formatted(PATH_TO_USERS_API), createdUserId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(UserDto.class);
        Assertions.assertNotNull(userDto.userPic());
        Assertions.assertEquals(String.format(FileUtils.USER_PIC_URN, createdUserId), userDto.userPic());
    }

    @Test
    void successfulDeleteUserPicTest() {
        loadUserPic();
        requestWithBearerToken()
                .when()
                .delete("/api/v1/files/user-pic/{userId}", createdUserId)
                .then()
                .statusCode(HttpStatus.SC_OK);
        String userPicUrn = String.format(FileUtils.USER_PIC_URN, createdUserId);
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> fileService.getFile(userPicUrn)
        );
    }

    private UserPicDto loadUserPic() {
        File imageFile = new File("src/test/resources/image.jpg");
        return requestWithBearerToken()
                .multiPart("image", imageFile, "multipart/form-data")
                .when()
                .post("/api/v1/files/user-pic/{userId}", createdUserId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(UserPicDto.class);
    }

}
