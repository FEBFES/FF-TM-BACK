package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.specification.UserSpec;
import com.febfes.fftmback.repository.UserViewRepository;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.util.DtoBuilders;
import net.kaczmarzyk.spring.data.jpa.utils.SpecificationBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

public class UserFilterTest extends BasicStaticDataTestClass {

    public static final String USER_DISPLAY_NAME = "test_display_name";

    @Autowired
    UserViewRepository userViewRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired AuthenticationService authenticationService
    ) {
        authenticationService.registerUser(DtoBuilders.createUser(USER_DISPLAY_NAME));
        authenticationService.registerUser(DtoBuilders.createUser("123" + USER_DISPLAY_NAME));
        authenticationService.registerUser(DtoBuilders.createUser(USER_DISPLAY_NAME + "456"));
        authenticationService.registerUser(DtoBuilders.createUser("1256"));
        authenticationService.registerUser(DtoBuilders.createUser("something"));
    }

    @ParameterizedTest
    @MethodSource("displayNameLikeFilterData")
    void displayNameLikeFilterTest(UserSpec userSpec, int expected) {
        Assertions.assertEquals(expected, userViewRepository.findAll(userSpec).size());
    }

    static Stream<Arguments> displayNameLikeFilterData() {
        return Stream.of(
                Arguments.of(getSpecForDisplayName(USER_DISPLAY_NAME), 3),
                Arguments.of(getSpecForDisplayName("123"), 1),
                Arguments.of(getSpecForDisplayName("456"), 1),
                Arguments.of(getSpecForDisplayName("12"), 2),
                Arguments.of(getSpecForDisplayName("56"), 2)
        );
    }

    private static UserSpec getSpecForDisplayName(String value) {
        return SpecificationBuilder.specification(UserSpec.class)
                .withParam("displayName", value)
                .build();
    }
}
