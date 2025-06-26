package com.febfes.fftmback.unit.authentication;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.febfes.fftmback.exception.TokenExpiredException;
import com.febfes.fftmback.service.AuthenticationServiceImpl;
import com.febfes.fftmback.unit.BaseUnitTest;
import com.febfes.fftmback.util.DateUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccessTokenExpirationTest extends BaseUnitTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private String mockToken(Duration offset) {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject("username")
                .setIssuedAt(DateUtils.getCurrentDate())
                .setExpiration(DateUtils.getCurrentDatePlusDuration(offset))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void testCheckAccessTokenExpirationWithInvalidToken() {
        String token = "invalid_token";
        assertThrows(JWTDecodeException.class, () -> authenticationService.checkAccessTokenExpiration(token));
    }

    @Test
    void testCheckAccessTokenExpirationWithExpiredToken() {
        String token = mockToken(Duration.ofSeconds(-5));
        assertThrows(TokenExpiredException.class, () -> authenticationService.checkAccessTokenExpiration(token));
    }

    @Test
    void testCheckAccessTokenExpirationWithValidToken() {
        String token = mockToken(Duration.ofSeconds(5));
        assertDoesNotThrow(() -> authenticationService.checkAccessTokenExpiration(token));
    }

}
