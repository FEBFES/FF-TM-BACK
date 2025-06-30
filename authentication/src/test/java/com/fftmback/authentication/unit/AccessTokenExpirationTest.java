package com.fftmback.authentication.unit;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.febfes.fftmback.util.DateUtils;
import com.fftmback.authentication.config.jwt.JwtService;
import com.fftmback.authentication.exception.TokenExpiredException;
import com.fftmback.authentication.repository.UserRepository;
import com.fftmback.authentication.service.RefreshTokenService;
import com.fftmback.authentication.service.impl.AuthenticationServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


class AccessTokenExpirationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckAccessTokenExpirationWithInvalidToken() {
        String token = "invalid_token";
        assertThrows(JWTDecodeException.class, () -> authenticationService.checkAccessTokenExpiration(token));
    }

    @Test
    void testCheckAccessTokenExpirationWithExpiredToken() {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String token = Jwts
                .builder()
                .setClaims(new HashMap<>())
                .setSubject("username")
                .setIssuedAt(DateUtils.getCurrentDate())
                .setExpiration(DateUtils.getCurrentDatePlusDuration(Duration.ofSeconds(-5)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        assertThrows(TokenExpiredException.class, () -> authenticationService.checkAccessTokenExpiration(token));
    }

    @Test
    void testCheckAccessTokenExpirationWithValidToken() {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String token = Jwts
                .builder()
                .setClaims(new HashMap<>())
                .setSubject("username")
                .setIssuedAt(DateUtils.getCurrentDate())
                .setExpiration(DateUtils.getCurrentDatePlusDuration(Duration.ofSeconds(5)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        assertDoesNotThrow(() -> authenticationService.checkAccessTokenExpiration(token));
    }

}
