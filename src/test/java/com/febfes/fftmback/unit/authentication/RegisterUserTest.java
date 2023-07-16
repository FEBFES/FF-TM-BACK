package com.febfes.fftmback.unit.authentication;

import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.exception.EntityAlreadyExistsException;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegisterUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser() {
        // Create a new user
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setEncryptedPassword("password");

        // Mock the userRepository to return false when checking for existing email or username
        when(userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())).thenReturn(false);

        // Mock the passwordEncoder to return a hashed password
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashedpassword");

        // Call the registerUser method
        authenticationService.registerUser(user);

        // Verify that the user was saved with the hashed password and a generated display name
        verify(userRepository).save(argThat(new UserMatcher(user)));
    }

    @Test
    public void testRegisterUserWithExistingEmailOrUsername() {
        // Create a new user
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setEncryptedPassword("password");

        // Mock the userRepository to return true when checking for existing email or username
        when(userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> authenticationService.registerUser(user));
    }

    private record UserMatcher(UserEntity expectedUser) implements ArgumentMatcher<UserEntity> {

        @Override
        public boolean matches(UserEntity actualUser) {
            return expectedUser.getEmail().equals(actualUser.getEmail())
                    && expectedUser.getUsername().equals(actualUser.getUsername())
                    && expectedUser.getPassword().equals(actualUser.getPassword())
                    && actualUser.getDisplayName() != null;
        }
    }
}
