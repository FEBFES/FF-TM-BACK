package com.febfes.fftmback.unit.authentication;

import com.febfes.fftmback.domain.dao.RefreshTokenEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.auth.GetAuthDto;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.service.AuthenticationServiceImpl;
import com.febfes.fftmback.service.JwtTestService;
import com.febfes.fftmback.service.RefreshTokenService;
import com.febfes.fftmback.unit.BaseUnitTest;
import com.febfes.fftmback.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.febfes.fftmback.util.UnitTestBuilders.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticateUserTest extends BaseUnitTest {

    private static final Long FIRST_ID = 1L;
    private static final String USERNAME = "test_user";
    private static final String USER_PASS = "password";
    private static final String USER_ENCODED_PASS = "encodedPassword";
    private static final String TOKEN = "refreshToken";
    private static final String JWT_TOKEN = "jwtToken";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTestService jwtTestService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;


    @Test
    void testAuthenticateUser() {
        UserEntity user = user(FIRST_ID, USERNAME, USER_PASS);
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .id(FIRST_ID)
                .token(TOKEN)
                .userEntity(user)
                .expiryDate(DateUtils.getCurrentLocalDateTime())
                .build();

        // Set up the mock objects to return the expected values
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(USER_PASS)).thenReturn(USER_ENCODED_PASS);
        when(jwtTestService.generateToken(user)).thenReturn(JWT_TOKEN);
        when(refreshTokenService.getRefreshTokenByUserId(FIRST_ID)).thenReturn(refreshToken);

        // Call the registerUser method
        authenticationService.registerUser(user);

        // Call the method being tested
        GetAuthDto result = authenticationService.authenticateUser(user);

        // Verify that the expected values were returned
        assertEquals(JWT_TOKEN, result.accessToken());
        assertEquals(FIRST_ID, result.userId().longValue());
        assertEquals(TOKEN, result.refreshToken());

        // Verify that the mock objects were called as expected
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(userRepository).findByUsername(USERNAME);
        verify(passwordEncoder).encode(USER_PASS);
        verify(jwtTestService).generateToken(user);
        verify(refreshTokenService).getRefreshTokenByUserId(FIRST_ID);
    }

}
