package com.assignmentService.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.assignmentservice.controller.AssignmentController;
import com.assignmentservice.dto.*;
import com.assignmentservice.model.TicketStatus;
import com.assignmentservice.service.AssignmentService;
import com.assignmentservice.service.SlaService;
import com.fasterxml.jackson.databind.ObjectMapper;

class AssignmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AssignmentService assignmentService;

    @Mock
    private SlaService slaService;

    @InjectMocks
    private AssignmentController assignmentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(assignmentController)
                .build();
        objectMapper = new ObjectMapper();
    }


    @Test
    void assignTicket_success() throws Exception {

        AssignmentRequest request = new AssignmentRequest();
        request.setTicketId("T1");
        request.setAgentId("A1");

        when(assignmentService.assign(any(), anyString()))
                .thenReturn("Ticket assigned");

        mockMvc.perform(
                post("/assignments/assign")
                        .header("X-USER-ID", "admin1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(content().string("Ticket assigned"));
    }


    @Test
    void getAgentWorkload_success() throws Exception {

        AgentWorkLoadResponse response =
                new AgentWorkLoadResponse("OPEN", 5L);

        when(assignmentService.getAgentWorkload("A1"))
                .thenReturn(List.of(response));

        mockMvc.perform(
                get("/assignments/agents/{agentId}/workload", "A1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].status").value("OPEN"))
        .andExpect(jsonPath("$[0].count").value(5));
    }

    @Test
    void getAllAgentsWorkload_success() throws Exception {

        AgentStatusCount count =
                new AgentStatusCount("A1", "OPEN", 3L);

        when(assignmentService.getAllAgentsWorkload())
                .thenReturn(List.of(count));

        mockMvc.perform(
                get("/assignments/manager/workload")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].agentId").value("A1"))
        .andExpect(jsonPath("$[0].status").value("OPEN"))
        .andExpect(jsonPath("$[0].count").value(3));
    }


    @Test
    void reassign_success() throws Exception {

        ReAssignment request = new ReAssignment();
        request.setTicketId("T1");
        request.setNewAgentId("A2");

        when(assignmentService.reassign(anyString(), any()))
                .thenReturn("Reassigned");

        mockMvc.perform(
                post("/assignments/reassign")
                        .header("X-USER-ID", "manager1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(content().string("Reassigned"));
    }

    @Test
    void updateSlaFromTicket_success() throws Exception {

        TicketStatusUpdateRequest request = new TicketStatusUpdateRequest();
        request.setTicketId("T1");
        request.setStatus(TicketStatus.RESOLVED);

        mockMvc.perform(
                put("/assignments/internal/sla/update-from-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
    }

    @Test
    void getManagerId_success() throws Exception {

        when(assignmentService.getManagerId("T1"))
                .thenReturn("M1");

        mockMvc.perform(
                get("/assignments/internal/{ticketId}/getManagerId", "T1")
        )
        .andExpect(status().isOk())
        .andExpect(content().string("M1"));
    }
}
