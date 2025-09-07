package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.dtos.UserDtoRequest;
import com.kephas.bookstoreapi.entities.User;
import com.kephas.bookstoreapi.entities.UserRole;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService unit tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDtoRequest userDto;

    @BeforeEach
    void setup() {
        user = new User(null, "John Doe", "john@example.com", "password123", UserRole.USER, LocalDateTime.now());

        userDto = new UserDtoRequest("John Updated", "john@example.com", "password123", "newPassword", UserRole.USER);
    }

    @Nested
    @DisplayName("update() test")
    class UpdateTest {
        @Test
        @DisplayName("Should update user details successfully")
        void update_ShouldUpdateUser() {
            when(userRepository.save(user)).thenReturn(user);

            User updated = userService.update(user, userDto);

            assertEquals("John Updated", updated.getName());
            assertEquals("john@example.com", updated.getEmail());
            verify(userRepository, times(1)).save(user);
        }
    }

    @Nested
    @DisplayName("changePassword() test")
    class ChangePasswordTest {
        @Test
        @DisplayName("Should change user password successfully")
        void changePassword_ShouldEncodeAndSave() {
            when(passwordEncoder.encode(userDto.newPassword())).thenReturn("encodedPassword");

            userService.changePassword(user, userDto);

            verify(authenticationManager, times(1)).authenticate(
                    argThat(token ->
                            token.getPrincipal().equals(user.getEmail()) &&
                                    token.getCredentials().equals(userDto.password())
                    )
            );
            assertEquals("encodedPassword", user.getPassword());
            verify(userRepository, times(1)).save(user);
        }
    }

    @Nested
    @DisplayName("makeAdmin() test")
    class MakeAdminTest {
        @Test
        @DisplayName("Should make user admin if not already")
        void makeAdmin_ShouldSetAdmin() {
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);

            String result = userService.makeAdmin(user.getId());

            assertEquals("John Doe is now an admin", result);
            assertEquals(UserRole.ADMIN, user.getRole());
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Should return message if already admin")
        void makeAdmin_ShouldReturnAlreadyAdmin() {
            user.setRole(UserRole.ADMIN);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            String result = userService.makeAdmin(user.getId());

            assertEquals("John Doe is already an admin", result);
            verify(userRepository, never()).save(user);
        }

        @Test
        @DisplayName("Should throw if user not found")
        void makeAdmin_ShouldThrow_WhenNotFound() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.makeAdmin(id));
        }
    }

    @Nested
    @DisplayName("unMakeAdmin() test")
    class UnMakeAdminTest {
        @Test
        @DisplayName("Should revoke admin role")
        void unMakeAdmin_ShouldSetUserRole() {
            user.setRole(UserRole.ADMIN);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);

            String result = userService.unMakeAdmin(user.getId());

            assertEquals("John Doe is no longer an admin", result);
            assertEquals(UserRole.USER, user.getRole());
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Should return message if already non-admin")
        void unMakeAdmin_ShouldReturnAlreadyNonAdmin() {
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            String result = userService.unMakeAdmin(user.getId());

            assertEquals("John Doe is already non admin", result);
            verify(userRepository, never()).save(user);
        }

        @Test
        @DisplayName("Should throw if user not found")
        void unMakeAdmin_ShouldThrow_WhenNotFound() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.unMakeAdmin(id));
        }
    }
}
