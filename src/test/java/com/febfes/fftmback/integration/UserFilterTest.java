package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.query.FilterRequest;
import com.febfes.fftmback.domain.common.query.FilterSpecification;
import com.febfes.fftmback.domain.common.query.Operator;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.service.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;

public class UserFilterTest extends BasicTestClass {

    public static final String USER_DISPLAY_NAME = "test_display_name";

    @Autowired
    UserRepository userRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired AuthenticationService authenticationService
    ) {
        authenticationService.registerUser(UserEntity.builder().email(USER_EMAIL).username(USER_USERNAME)
                .encryptedPassword(USER_PASSWORD).displayName(USER_DISPLAY_NAME).build());
        authenticationService.registerUser(UserEntity.builder().email(USER_EMAIL + "1").username(USER_USERNAME + "1")
                .encryptedPassword(USER_PASSWORD).displayName("123" + USER_DISPLAY_NAME).build());
        authenticationService.registerUser(UserEntity.builder().email(USER_EMAIL + "2").username(USER_USERNAME + "2")
                .encryptedPassword(USER_PASSWORD).displayName(USER_DISPLAY_NAME + "456").build());
        authenticationService.registerUser(UserEntity.builder().email(USER_EMAIL + "3").username(USER_USERNAME + "3")
                .encryptedPassword(USER_PASSWORD).displayName("1256").build());
        authenticationService.registerUser(UserEntity.builder().email(USER_EMAIL + "4").username(USER_USERNAME + "4")
                .encryptedPassword(USER_PASSWORD).displayName("something").build());
    }

    @Test
    void displayNameLikeFilterTest() {
        List<FilterRequest> filters1st = getFilterListForLikeDisplayName(USER_DISPLAY_NAME);
        Assertions.assertEquals(3, userRepository.findAll(new FilterSpecification<>(filters1st)).size());

        List<FilterRequest> filters2nd = getFilterListForLikeDisplayName("123");
        Assertions.assertEquals(1, userRepository.findAll(new FilterSpecification<>(filters2nd)).size());

        List<FilterRequest> filters3rd = getFilterListForLikeDisplayName("456");
        Assertions.assertEquals(1, userRepository.findAll(new FilterSpecification<>(filters3rd)).size());

        List<FilterRequest> filters4th = getFilterListForLikeDisplayName("12");
        Assertions.assertEquals(2, userRepository.findAll(new FilterSpecification<>(filters4th)).size());

        List<FilterRequest> filters5th = getFilterListForLikeDisplayName("56");
        Assertions.assertEquals(2, userRepository.findAll(new FilterSpecification<>(filters5th)).size());
    }

    private List<FilterRequest> getFilterListForLikeDisplayName(String value) {
        return List.of(
                FilterRequest.builder()
                        .property("displayName")
                        .operator(Operator.LIKE)
                        .value(value)
                        .build()
        );
    }
}
