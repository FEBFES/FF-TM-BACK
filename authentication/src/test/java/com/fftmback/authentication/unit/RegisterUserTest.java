package com.fftmback.authentication.unit;

import com.febfes.fftmback.exception.EntityAlreadyExistsException;
import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.repository.UserRepository;
import com.fftmback.authentication.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterUserTest extends BaseUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;


    @Test
    void testRegisterUser() {
        UserEntity user = user(null, "testuser", "password");
        user.setEmail("test@example.com");

        // Mock the userRepository to return false when checking for existing email or username
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        // Mock the passwordEncoder to return a hashed password
        when(passwordEncoder.encode("password")).thenReturn("hashedpassword");

        // Call the registerUser method
        authenticationService.registerUser(user);

        // Verify that the user was saved with the hashed password and a generated display name
        verify(userRepository).save(argThat(new UserMatcher(user)));
    }

    @Test
    void testRegisterUserWithExistingEmailOrUsername() {
        UserEntity user = user(null, "testuser", "password");
        user.setEmail("test@example.com");

        // Mock the userRepository to return true when checking for existing email or username
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

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
