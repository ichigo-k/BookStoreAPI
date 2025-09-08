package com.kephas.bookstoreapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kephas.bookstoreapi.dtos.UserDtoRequest;
import com.kephas.bookstoreapi.dtos.UserDtoResponse;
import com.kephas.bookstoreapi.entities.User;
import com.kephas.bookstoreapi.mappers.UserMapper;
import com.kephas.bookstoreapi.services.UserService;
import com.kephas.bookstoreapi.utils.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User mockUser;
    private UserDtoResponse userDtoResponse;
    private UserDtoRequest userDtoRequest;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setName("testuser");
        mockUser.setEmail("test@example.com");

        userDtoResponse = new UserDtoResponse(null, mockUser.getName(), mockUser.getEmail(), null);
        userDtoRequest = new UserDtoRequest("updatedUser", "updated@example.com", "Password123", "NewPassword123", null);
    }

    @Nested
    @DisplayName("GET /api/v1/user/profile")
    class Profile {
        @Test
        @DisplayName("Should return 200 OK with user profile")
        @WithMockUser(username = "testuser", roles = "USER")
        void shouldReturnUserProfile() throws Exception {
            when(userMapper.toDto(any(User.class))).thenReturn(userDtoResponse);

            mockMvc.perform(get("/api/v1/user/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/user/")
    class UpdateDetails {
        @Test
        @DisplayName("Should update user details and return 200 OK")
        @WithMockUser(username = "testuser", roles = "USER")
        void shouldUpdateUserDetails() throws Exception {
            when(userService.update(any(User.class), any(UserDtoRequest.class))).thenReturn(mockUser);
            when(userMapper.toDto(any(User.class))).thenReturn(userDtoResponse);

            mockMvc.perform(patch("/api/v1/user/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDtoRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Details update successfully"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/user/change-password")
    class ChangePassword {
        @Test
        @DisplayName("Should change password successfully and return 200 OK")
        @WithMockUser(username = "testuser", roles = "USER")
        void shouldChangePasswordSuccessfully() throws Exception {
            doNothing().when(userService).changePassword(any(User.class), any(UserDtoRequest.class));

            mockMvc.perform(post("/api/v1/user/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDtoRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Password changed successfully"));
        }
    }

    @Nested
    @DisplayName("Admin endpoints")
    class AdminEndpoints {
        @Test
        @DisplayName("Should promote a user to admin and return 200 OK")
        @WithMockUser(username = "admin", roles = "ADMIN")
        void shouldMakeAdminSuccessfully() throws Exception {
            UUID id = UUID.randomUUID();
            when(userService.makeAdmin(id)).thenReturn("User promoted to admin");

            mockMvc.perform(get("/api/v1/user/make-admin/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("User promoted to admin"));
        }

        @Test
        @DisplayName("Should revoke admin privileges and return 200 OK")
        @WithMockUser(username = "admin", roles = "ADMIN")
        void shouldUnmakeAdminSuccessfully() throws Exception {
            UUID id = UUID.randomUUID();
            when(userService.unMakeAdmin(id)).thenReturn("Admin privileges revoked");

            mockMvc.perform(get("/api/v1/user/unmake-admin/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Admin privileges revoked"));
        }
    }
}
