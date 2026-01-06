package com.assignmentService.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.assignmentService.dto.SlaEventResponse;
import com.assignmentService.service.SlaEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

class SlaEventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SlaEventService slaEventService;

    @InjectMocks
    private SlaEventController slaEventController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        MappingJackson2HttpMessageConverter jacksonConverter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(slaEventController)
                .setMessageConverters(jacksonConverter)
                .build();
    }

    @Test
    void getAllEvents_success() throws Exception {

        SlaEventResponse event = new SlaEventResponse(
                1L,
                "ASSIGN1",
                "TICKET1",
                "AGENT1",
                "ESCALATED",
                LocalDateTime.now(),
                "Response time breached"
        );

        Page<SlaEventResponse> page =
                new PageImpl<>(
                    new ArrayList<>(List.of(event)),
                    PageRequest.of(0, 10),
                    1
                );

        when(slaEventService.getAllEvents(
                anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        mockMvc.perform(
                get("/sla-events")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "occurredAt")
                        .param("direction", "DESC")
                        .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].eventId").value(1))
        .andExpect(jsonPath("$.content[0].ticketId").value("TICKET1"))
        .andExpect(jsonPath("$.content[0].agentId").value("AGENT1"))
        .andExpect(jsonPath("$.content[0].eventType").value("ESCALATED"));
    }



    @Test
    void getAgentEvents_success() throws Exception {

        SlaEventResponse event = new SlaEventResponse(
                2L,
                "ASSIGN2",
                "TICKET2",
                "AGENT1",
                "BREACHED",
                LocalDateTime.now(),
                "Resolution time breached"
        );

        when(slaEventService.getEventsByAgent("AGENT1"))
                .thenReturn(List.of(event));

        mockMvc.perform(
                get("/sla-events/agent/{agentId}", "AGENT1")
                        .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].eventId").value(2))
        .andExpect(jsonPath("$[0].eventType").value("BREACHED"))
        .andExpect(jsonPath("$[0].remarks").value("Resolution time breached"));
    }
}
