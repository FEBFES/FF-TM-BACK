package com.febfes.fftmback.unit.authentication;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.febfes.fftmback.config.jwt.JwtService;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.exception.TokenExpiredException;
import com.febfes.fftmback.service.impl.AuthenticationServiceImpl;
import com.febfes.fftmback.util.DateUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

class AccessTokenExpirationTest {

    @Mock
    private JwtService jwtService;

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
        when(jwtService.generateToken(anyMap(), any(UserEntity.class))).thenReturn(
                Jwts
                        .builder()
                        .setClaims(new HashMap<>())
                        .setSubject("username")
                        .setIssuedAt(DateUtils.getCurrentDate())
                        .setExpiration(DateUtils.getCurrentDatePlusDuration(Duration.ofSeconds(-5)))
                        .signWith(secretKey, SignatureAlgorithm.HS256)
                        .compact()
        );
        String token = jwtService.generateToken(new HashMap<>(), new UserEntity());
        assertThrows(TokenExpiredException.class, () -> authenticationService.checkAccessTokenExpiration(token));
    }

    @Test
    void testCheckAccessTokenExpirationWithValidToken() {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        when(jwtService.generateToken(anyMap(), any(UserEntity.class))).thenReturn(
                Jwts
                        .builder()
                        .setClaims(new HashMap<>())
                        .setSubject("username")
                        .setIssuedAt(DateUtils.getCurrentDate())
                        .setExpiration(DateUtils.getCurrentDatePlusDuration(Duration.ofSeconds(5)))
                        .signWith(secretKey, SignatureAlgorithm.HS256)
                        .compact()
        );
        String token = jwtService.generateToken(new HashMap<>(), new UserEntity());
        assertDoesNotThrow(() -> authenticationService.checkAccessTokenExpiration(token));
    }

}
