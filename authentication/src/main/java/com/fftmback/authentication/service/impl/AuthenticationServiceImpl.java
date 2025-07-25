package com.fftmback.authentication.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.febfes.fftmback.dto.ErrorType;
import com.febfes.fftmback.exception.EntityAlreadyExistsException;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.fftmback.authentication.config.jwt.JwtService;
import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.dto.ConnValidationResponse;
import com.fftmback.authentication.dto.GetAuthDto;
import com.fftmback.authentication.exception.TokenExpiredException;
import com.fftmback.authentication.feign.RoleClient;
import com.fftmback.authentication.repository.UserRepository;
import com.fftmback.authentication.service.AuthenticationService;
import com.fftmback.authentication.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final RoleClient roleClient;

    private final RandomStringGenerator generator = new RandomStringGenerator.Builder()
            .selectFrom('0', '9')
            .build();

    private static final String USER_STRING = "user";

    private static final String BEARER = "Bearer ";
    private static final String PROJECTS_REGEX = "/projects/(\\d+).*";
    private static final Pattern PROJECTS_PATTERN = Pattern.compile(PROJECTS_REGEX);

    @Value("${custom-headers.init-uri}")
    private String initUriHeader;

    private String generateDisplayName() {
        return USER_STRING + generator.generate(6); // generate a 6-character username
    }

    @Override
    public void registerUser(UserEntity user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityAlreadyExistsException(UserEntity.ENTITY_NAME,
                    "email", user.getEmail(), ErrorType.AUTH);
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EntityAlreadyExistsException(UserEntity.ENTITY_NAME,
                    "username", user.getUsername(), ErrorType.AUTH);
        }
        user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
        user.setDisplayName(isNull(user.getDisplayName()) ? generateDisplayName() : user.getDisplayName());
        userRepository.save(user);
        log.info("User with username = {} saved", user.getUsername());
    }

    @Override
    public GetAuthDto authenticateUser(UserEntity user) {
        UserEntity receivedUser = getUserByUsername(user.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        Long userId = receivedUser.getId();

        String jwtToken = jwtService.generateToken(receivedUser);

        String refreshToken = refreshTokenService.getRefreshTokenByUserId(userId);
        return GetAuthDto.builder()
                .accessToken(jwtToken)
                .userId(userId)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void checkAccessTokenExpiration(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            Date expiresAt = decodedJWT.getExpiresAt();
            if (expiresAt.before(new Date())) {
                throw new TokenExpiredException(token);
            }
        } catch (JWTDecodeException e) {
            log.error("checkAccessTokenExpiration throws JWTDecodeException", e);
            throw e;
        }
    }

    @Override
    public ConnValidationResponse validateToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String initUri = request.getHeader(initUriHeader);
        final String jwt = authHeader.substring(BEARER.length());
        final String username = jwtService.extractUsername(jwt);
        UserEntity userDetails = getUserByUsername(username);
        boolean isAuthenticated = jwtService.isTokenValid(jwt, userDetails);
        String role = extractUserRoleFromInitUri(initUri, userDetails);

        return ConnValidationResponse.builder()
                .status("OK")
                .methodType(HttpMethod.GET.name())
                .username(username)
                .isAuthenticated(isAuthenticated)
                .role(role)
                .build();
    }

    private UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, "username", username, ErrorType.AUTH));
    }

    private String extractUserRoleFromInitUri(String initUri, UserEntity userDetails) {
        if (initUri == null) {
            return null;
        }

        Matcher matcher = PROJECTS_PATTERN.matcher(initUri);

        if (matcher.find()) {
            String projectIdStr = matcher.group(1);
            try {
                Long projectId = Long.parseLong(projectIdStr);
                return roleClient.getUserRoleNameOnProject(projectId, userDetails.getId()).name();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid project ID format in init URI", e);
            }
        }
        return null;
    }

}
