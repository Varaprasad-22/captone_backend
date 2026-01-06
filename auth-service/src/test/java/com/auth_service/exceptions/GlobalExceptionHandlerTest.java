package com.auth_service.exceptions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import com.auth_service.exception.CannotCreateRoleException;
import com.auth_service.exception.GlobalExceptionHandler;
import com.auth_service.exception.InvalidCredentialsException;
import com.auth_service.exception.RoleNotFoundException;
import com.auth_service.exception.UserAlreadyExistsException;
import com.auth_service.exception.UserDisabledException;
import com.auth_service.exception.UserNotFoundException;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleUserNotFound() throws Exception {
        mockMvc.perform(get("/test/user-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    void handleInvalidCredentials() throws Exception {
        mockMvc.perform(get("/test/invalid-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }


    @Test
    void handleUserDisabled() throws Exception {
        mockMvc.perform(get("/test/user-disabled"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User disabled"));
    }


    @Test
    void handleUserAlreadyExists() throws Exception {
        mockMvc.perform(get("/test/user-exists"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already exists"));
    }


    @Test
    void handleRoleNotFound() throws Exception {
        mockMvc.perform(get("/test/role-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Role not found"));
    }


    @Test
    void handleCannotCreateRole() throws Exception {
        mockMvc.perform(get("/test/cannot-create-role"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Cannot create role"));
    }

    @Test
    void handleGenericException() throws Exception {
        mockMvc.perform(get("/test/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Something went wrong"));
    }
    
    @RestController
    static class DummyController {

        @GetMapping("/test/user-not-found")
        public void userNotFound() {
            throw new UserNotFoundException("User not found");
        }

        @GetMapping("/test/invalid-credentials")
        public void invalidCredentials() {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        @GetMapping("/test/user-disabled")
        public void userDisabled() {
            throw new UserDisabledException("User disabled");
        }

        @GetMapping("/test/user-exists")
        public void userExists() {
            throw new UserAlreadyExistsException("User already exists");
        }

        @GetMapping("/test/role-not-found")
        public void roleNotFound() {
            throw new RoleNotFoundException("Role not found");
        }

        @GetMapping("/test/cannot-create-role")
        public void cannotCreateRole() {
            throw new CannotCreateRoleException("Cannot create role");
        }

        @GetMapping("/test/validation")
        public void validation(@Valid TestDto dto) {

        }

        @GetMapping("/test/generic")
        public void generic() {
            throw new RuntimeException("Something went wrong");
        }
    }

    static class TestDto {
        @NotBlank(message = "Name is required")
        private String name;
    }
}
