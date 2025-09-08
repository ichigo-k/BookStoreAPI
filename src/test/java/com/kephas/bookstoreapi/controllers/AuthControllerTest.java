package com.kephas.bookstoreapi.controllers;

import com.kephas.bookstoreapi.dtos.UserDtoRequest;
import com.kephas.bookstoreapi.entities.UserRole;
import com.kephas.bookstoreapi.services.AuthService;
import com.kephas.bookstoreapi.utils.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDtoRequest userDtoRequest;

    @BeforeEach
    void setup() {
        userDtoRequest = new UserDtoRequest(
                "Test User",
                "test@example.com",
                "password123",
                "Newpassword123",
                UserRole.USER
        );
    }

    @Nested
    @DisplayName("POST /api/v1/auth/signup")
    class SignUp {

        @Test
        @DisplayName("SignUp - should return 201 and success message")
        void signUp_success() throws Exception {
            Mockito.doNothing().when(authService).signUp(any(UserDtoRequest.class));

            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDtoRequest)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("Account registered successfully"))
                    .andExpect(jsonPath("$.statusCode").value(201));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class Login {

        @Test
        @DisplayName("Login - should return 200, token, and set cookie")
        void login_success() throws Exception {
            Map<String, Object> loginData = new HashMap<>();
            loginData.put("token", "mocked-jwt-token");
            loginData.put("cookie", "token=mocked-jwt-token; Path=/; HttpOnly");

            when(authService.login(any(UserDtoRequest.class))).thenReturn(loginData);

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDtoRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(header().string("Set-Cookie", "token=mocked-jwt-token; Path=/; HttpOnly"))
                    .andExpect(jsonPath("$.message").value("Login successful"))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.data").value("mocked-jwt-token"));
        }

        @Test
        @DisplayName("Login - should return 401 for invalid credentials")
        void login_failed() throws Exception {
            when(authService.login(any(UserDtoRequest.class)))
                    .thenThrow(new BadCredentialsException("Invalid email or password"));

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDtoRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid email or password"))
                    .andExpect(jsonPath("$.statusCode").value(401));
        }
    }
}
