package com.auth_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.auth_service.dto.*;
import com.auth_service.model.Erole;
import com.auth_service.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_success() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setName("User");
        request.setEmail("user@test.com");
        request.setPassword("password123");

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(content().string("User registered successfully"));
    }

    @Test
    void login_success() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse("jwt-token"));

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void adminRegister_success() throws Exception {

        AdminCreationRequest request = new AdminCreationRequest();
        request.setName("Admin");
        request.setEmail("admin@test.com");
        request.setPassword("admin123");
        request.setRole(Erole.ROLE_ADMIN);

        mockMvc.perform(
                post("/auth/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(content().string("User registered successfully"));
    }

    @Test
    void getAllUsers_success() throws Exception {


        when(authService.getAllUsers(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(null);

        mockMvc.perform(get("/auth/getAll"))
                .andExpect(status().isOk());
                
    }

    @Test
    void getAllAgents_success() throws Exception {


        when(authService.getAllAgents(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(null);

        mockMvc.perform(get("/auth/getAgents"))
                .andExpect(status().isOk());
    }

    @Test
    void getUsersByRole_success() throws Exception {

 
        when(authService.getUsersByRole(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(null);

        mockMvc.perform(get("/auth/users/{role}", "ROLE_USER"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_success() throws Exception {

        UserInfoResponse response = new UserInfoResponse(
                "U1", "user@test.com", "User", true, "ROLE_USER"
        );

        when(authService.getUsersById("U1"))
                .thenReturn(response);

        mockMvc.perform(get("/auth/getEmail/{userId}", "U1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    void deactivateUser_success() throws Exception {

        mockMvc.perform(
                put("/auth/deactivate/{userId}", "U1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("active", false)))
        )
        .andExpect(status().isNoContent());
    }

    @Test
    void updateUserRole_success() throws Exception {

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRole(Erole.ROLE_ADMIN);

        mockMvc.perform(
                put("/auth/users/{userId}/role", "U1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isNoContent());
    }
}
