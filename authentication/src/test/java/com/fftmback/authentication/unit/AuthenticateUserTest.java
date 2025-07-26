package com.fftmback.authentication.unit;

import com.febfes.fftmback.util.DateUtils;
import com.fftmback.authentication.config.jwt.JwtService;
import com.fftmback.authentication.domain.RefreshTokenEntity;
import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.dto.GetAuthDto;
import com.fftmback.authentication.repository.UserRepository;
import com.fftmback.authentication.service.RefreshTokenService;
import com.fftmback.authentication.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticateUserTest {

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
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticateUser() {
        // Create a mock UserEntity object
        UserEntity user = new UserEntity();
        user.setId(FIRST_ID);
        user.setUsername(USERNAME);
        user.setEncryptedPassword(USER_PASS);

        // Create a mock RefreshTokenEntity object
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setId(FIRST_ID);
        refreshToken.setToken(TOKEN);
        refreshToken.setUserEntity(user);
        refreshToken.setExpiryDate(DateUtils.getCurrentLocalDateTime());

        // Set up the mock objects to return the expected values
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(user.getPassword())).thenReturn(USER_ENCODED_PASS);
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn(JWT_TOKEN);
        when(refreshTokenService.getRefreshTokenByUserId(anyLong())).thenReturn(TOKEN);

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
        verify(userRepository).findByUsername(anyString());
        verify(passwordEncoder).encode(anyString());
        verify(jwtService).generateToken(any(UserEntity.class));
        verify(refreshTokenService).getRefreshTokenByUserId(anyLong());
    }

}
