package com.assignmentService.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import com.assignmentservice.exception.AgentUnavailableException;
import com.assignmentservice.exception.AssignmentAlreadyExistsException;
import com.assignmentservice.exception.AssignmentNotFoundException;
import com.assignmentservice.exception.GlobalExceptionHandler;

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
    void handleAssignmentNotFound() throws Exception {
        mockMvc.perform(get("/test/assignment-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Assignment not found"));
    }

    @Test
    void handleAgentUnavailable() throws Exception {
        mockMvc.perform(get("/test/agent-unavailable"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Agent unavailable"));
    }

    @Test
    void handleAssignmentAlreadyExists() throws Exception {
        mockMvc.perform(get("/test/already-exists"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Already assigned"));
    }




    @Test
    void handleGenericException() throws Exception {
        mockMvc.perform(get("/test/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Something went wrong"));
    }


    @RestController
    static class DummyController {

        @GetMapping("/test/assignment-not-found")
        public void assignmentNotFound() {
            throw new AssignmentNotFoundException("Assignment not found");
        }

        @GetMapping("/test/agent-unavailable")
        public void agentUnavailable() {
            throw new AgentUnavailableException("Agent unavailable");
        }

        @GetMapping("/test/already-exists")
        public void alreadyExists() {
            throw new AssignmentAlreadyExistsException("Already assigned");
        }

        @GetMapping("/test/validation")
        public void validation(@Valid TestDto dto) {
            // validation auto-triggered
        }

        @GetMapping("/test/generic")
        public void generic() {
            throw new RuntimeException("Something went wrong");
        }
    }

    static class TestDto {
        @NotBlank(message = "Ticket ID is required")
        private String ticketId;
    }
}
