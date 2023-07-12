package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.query.FilterRequest;
import com.febfes.fftmback.domain.common.query.FilterSpecification;
import com.febfes.fftmback.domain.common.query.Operator;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.repository.UserViewRepository;
import com.febfes.fftmback.service.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;

public class UserFilterTest extends BasicStaticDataTestClass {

    public static final String USER_DISPLAY_NAME = "test_display_name";

    @Autowired
    UserViewRepository userViewRepository;

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

    @ParameterizedTest
    @MethodSource("displayNameLikeFilterData")
    void displayNameLikeFilterTest(List<FilterRequest> filters, int expected) {
        Assertions.assertEquals(expected, userViewRepository.findAll(new FilterSpecification<>(filters)).size());
    }

    static Stream<Arguments> displayNameLikeFilterData() {
        return Stream.of(
                Arguments.of(getFilterListForLikeDisplayName(USER_DISPLAY_NAME), 3),
                Arguments.of(getFilterListForLikeDisplayName("123"), 1),
                Arguments.of(getFilterListForLikeDisplayName("456"), 1),
                Arguments.of(getFilterListForLikeDisplayName("12"), 2),
                Arguments.of(getFilterListForLikeDisplayName("56"), 2)
        );
    }

    private static List<FilterRequest> getFilterListForLikeDisplayName(String value) {
        return List.of(
                FilterRequest.builder()
                        .property("displayName")
                        .operator(Operator.LIKE)
                        .value(value)
                        .build()
        );
    }
}
