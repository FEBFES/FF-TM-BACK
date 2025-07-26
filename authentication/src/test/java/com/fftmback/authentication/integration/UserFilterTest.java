package com.fftmback.authentication.integration;

import com.fftmback.authentication.domain.spec.UserSpec;
import com.fftmback.authentication.repository.UserRepository;
import com.fftmback.authentication.service.AuthenticationService;
import com.fftmback.authentication.util.DatabaseCleanup;
import com.fftmback.authentication.util.DtoBuilders;
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
    UserRepository userRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired AuthenticationService authenticationService,
            @Autowired DatabaseCleanup databaseCleanup
    ) {
        databaseCleanup.execute();
        authenticationService.registerUser(DtoBuilders.createUser(USER_DISPLAY_NAME));
        authenticationService.registerUser(DtoBuilders.createUser("123" + USER_DISPLAY_NAME));
        authenticationService.registerUser(DtoBuilders.createUser(USER_DISPLAY_NAME + "456"));
        authenticationService.registerUser(DtoBuilders.createUser("1256"));
        authenticationService.registerUser(DtoBuilders.createUser("something"));
    }

    @ParameterizedTest
    @MethodSource("displayNameLikeFilterData")
    void displayNameLikeFilterTest(UserSpec userSpec, int expected) {
        Assertions.assertEquals(expected, userRepository.findAll(userSpec).size());
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
