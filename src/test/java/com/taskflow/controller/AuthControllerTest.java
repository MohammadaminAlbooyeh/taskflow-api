package com.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.model.dto.AuthResponse;
import com.taskflow.model.dto.LoginRequest;
import com.taskflow.model.dto.RegisterRequest;
import com.taskflow.model.dto.UserDTO;
import com.taskflow.config.SecurityConfig;
import com.taskflow.security.JwtTokenProvider;
import com.taskflow.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void register_withValidRequest_returns201() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@taskflow.dev");
        request.setPassword("password123");
        request.setFullName("New User");

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .tokenType("Bearer")
                .user(UserDTO.builder().id(1L).email("new@taskflow.dev").fullName("New User").build())
                .build();

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_withBlankEmail_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("");
        request.setPassword("password123");
        request.setFullName("New User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withValidCredentials_returns200() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("new@taskflow.dev");
        request.setPassword("password123");

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .tokenType("Bearer")
                .user(UserDTO.builder().id(1L).email("new@taskflow.dev").build())
                .build();

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
