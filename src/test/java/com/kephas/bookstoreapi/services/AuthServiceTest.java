package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.dtos.UserDtoRequest;
import com.kephas.bookstoreapi.entities.User;
import com.kephas.bookstoreapi.entities.UserRole;
import com.kephas.bookstoreapi.exceptions.UniqueConstraintViolationException;
import com.kephas.bookstoreapi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService unit tests")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserDtoRequest userDto;

    @BeforeEach
    void setup() {
        userDto = new UserDtoRequest("John Doe", "john@example.com", "password123", null, UserRole.USER);
    }

    @Nested
    @DisplayName("signUp() tests")
    class SignUpTest {

        @Test
        @DisplayName("Should create a new user when email is not taken")
        void signUp_ShouldSaveUser_WhenEmailNotTaken() {
            when(userRepository.existsByEmail(userDto.email())).thenReturn(false);
            when(passwordEncoder.encode(userDto.password())).thenReturn("encodedPassword");

            authService.signUp(userDto);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository, times(1)).save(userCaptor.capture());

            User saved = userCaptor.getValue();
            assertEquals("John Doe", saved.getName());
            assertEquals("john@example.com", saved.getEmail());
            assertEquals("encodedPassword", saved.getPassword());
        }

        @Test
        @DisplayName("Should throw exception if email is already taken")
        void signUp_ShouldThrow_WhenEmailExists() {
            when(userRepository.existsByEmail(userDto.email())).thenReturn(true);

            assertThrows(UniqueConstraintViolationException.class, () -> authService.signUp(userDto));
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("login() tests")
    class LoginTest {

        @Test
        @DisplayName("Should return token and cookie on successful login")
        void login_ShouldReturnTokenAndCookie() {
            Authentication mockAuth = mock(Authentication.class);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuth);
            when(mockAuth.getName()).thenReturn(userDto.email());
            when(jwtService.generateToken(userDto.email())).thenReturn("jwtToken123");

            Map<String, Object> response = authService.login(userDto);

            assertEquals("jwtToken123", response.get("token"));
            assertTrue(response.get("cookie") instanceof ResponseCookie);

            ResponseCookie cookie = (ResponseCookie) response.get("cookie");
            assertEquals("jwt", cookie.getName());
            assertEquals("jwtToken123", cookie.getValue());

            verify(authenticationManager, times(1)).authenticate(any());
            verify(jwtService, times(1)).generateToken(userDto.email());
        }
    }
}
