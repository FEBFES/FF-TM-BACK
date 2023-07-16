package com.febfes.fftmback.unit.authentication;

import com.febfes.fftmback.config.jwt.JwtService;
import com.febfes.fftmback.domain.dao.RefreshTokenEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.auth.GetAuthDto;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.service.RefreshTokenService;
import com.febfes.fftmback.service.impl.AuthenticationServiceImpl;
import com.febfes.fftmback.util.DateUtils;
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
import static org.mockito.Mockito.*;

public class AuthenticateUserTest {

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
    public void testAuthenticateUser() {
        // Create a mock UserEntity object
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEncryptedPassword("password");

        // Create a mock RefreshTokenEntity object
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setId(1L);
        refreshToken.setToken("refreshToken");
        refreshToken.setUserEntity(user);
        refreshToken.setExpiryDate(DateUtils.getCurrentDate());

        // Set up the mock objects to return the expected values
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("jwtToken");
        when(refreshTokenService.getOrCreateRefreshToken(anyLong())).thenReturn(refreshToken);

        // Call the registerUser method
        authenticationService.registerUser(user);

        // Call the method being tested
        GetAuthDto result = authenticationService.authenticateUser(user);

        // Verify that the expected values were returned
        assertEquals("jwtToken", result.accessToken());
        assertEquals(1L, result.userId().longValue());
        assertEquals("refreshToken", result.refreshToken());

        // Verify that the mock objects were called as expected
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(jwtService, times(1)).generateToken(any(UserEntity.class));
        verify(refreshTokenService, times(1)).getOrCreateRefreshToken(anyLong());
    }

}
