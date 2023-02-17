package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.config.jwt.JwtService;
import com.febfes.fftmback.domain.RefreshTokenEntity;
import com.febfes.fftmback.domain.Role;
import com.febfes.fftmback.domain.UserEntity;
import com.febfes.fftmback.dto.auth.RefreshTokenDto;
import com.febfes.fftmback.dto.auth.TokenDto;
import com.febfes.fftmback.dto.auth.UserDetailsDto;
import com.febfes.fftmback.exception.EntityAlreadyExistsException;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.mapper.UserMapper;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.RefreshTokenService;
import com.febfes.fftmback.util.DateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final DateProvider dateProvider;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenDto registerUser(UserDetailsDto userDetailsDto) {
        if (userRepository.existsByEmailOrUsername(userDetailsDto.email(), userDetailsDto.username())) {
            throw new EntityAlreadyExistsException(UserEntity.class.getSimpleName());
        }

        UserEntity user = UserMapper.INSTANCE.userDetailsDtoToUser(
                userDetailsDto,
                dateProvider.getCurrentDate(),
                passwordEncoder.encode(userDetailsDto.password()),
                Role.MEMBER
        );

        userRepository.save(user);
        log.info("User saved: {}", user);
        String jwtToken = jwtService.generateToken(user);
        return new TokenDto(jwtToken);
    }

    @Override
    public RefreshTokenDto authenticateUser(UserDetailsDto userDetailsDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDetailsDto.username(),
                        userDetailsDto.password()
                )
        );
        UserEntity user = userRepository.findByUsername(userDetailsDto.username())
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getSimpleName(),
                        "username", userDetailsDto.username()));

        String jwtToken = jwtService.generateToken(user);

        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user.getId());
        log.info("User authenticated");
        return new RefreshTokenDto(jwtToken, refreshToken.getToken());
    }
}
