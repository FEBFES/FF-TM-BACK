package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.dto.UserPicDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.integration.basic.BasicTestClass;
import com.febfes.fftmback.service.FileService;
import com.febfes.fftmback.util.FileUrnUtils;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

class FileControllerTest extends BasicTestClass {

    @TempDir
    static File tempDir;

    @Autowired
    private FileService fileService;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("folders.user-pic",
                () -> String.format("%s/", tempDir.getPath())
        );
    }

    @Test
    void successfulLoadUserPicTest() {
        UserPicDto userPicDto = loadUserPic();
        Assertions.assertEquals(FileUrnUtils.getUserPicUrn(createdUserId), userPicDto.fileUrn());
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
    @Disabled("It needs to have UserClient in testcontainers")
    void successfulGetUserPicFromUserInfoTest() {
        loadUserPic();
        UserDto userDto = requestWithBearerToken()
                .when()
                .get("/api/v1/users/{id}", createdUserId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(UserDto.class);
        Assertions.assertNotNull(userDto.userPic());
        Assertions.assertEquals(FileUrnUtils.getUserPicUrn(createdUserId), userDto.userPic());
    }

    @Test
    void successfulDeleteUserPicTest() {
        loadUserPic();
        requestWithBearerToken()
                .when()
                .delete("/api/v1/files/user-pic/{userId}", createdUserId)
                .then()
                .statusCode(HttpStatus.SC_OK);
        String userPicUrn = FileUrnUtils.getUserPicUrn(createdUserId);
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
