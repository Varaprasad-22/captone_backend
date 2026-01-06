package com.assignmentService.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.assignmentservice.dto.SlaEventResponse;
import com.assignmentservice.model.SlaEvent;
import com.assignmentservice.repositories.SlaEventRepository;
import com.assignmentservice.service.SlaEventserviceImpl;

class SlaEventserviceImplTest {

    @Mock
    private SlaEventRepository eventRepository;

    @InjectMocks
    private SlaEventserviceImpl slaEventService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getAllEvents_success() {

        SlaEvent event = new SlaEvent();
        event.setEventId(1L);
        event.setAssignmentId("ASSIGN1");
        event.setTicketId("TICKET1");
        event.setAgentId("AGENT1");
        event.setEventType("ESCALATED");
        event.setOccurredAt(LocalDateTime.now());
        event.setRemarks("Response time breached");

        Page<SlaEvent> page = new PageImpl<>(List.of(event));

        when(eventRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        Page<SlaEventResponse> result =
                slaEventService.getAllEvents(0, 10, "occurredAt", "DESC");

        assertEquals(1, result.getTotalElements());

        SlaEventResponse response = result.getContent().get(0);
        assertEquals("ASSIGN1", response.getAssignmentId());
        assertEquals("TICKET1", response.getTicketId());
        assertEquals("AGENT1", response.getAgentId());
        assertEquals("ESCALATED", response.getEventType());
        assertEquals("Response time breached", response.getRemarks());
    }


    @Test
    void getEventsByAgent_success() {

        SlaEvent event = new SlaEvent();
        event.setEventId(2L);
        event.setAssignmentId("ASSIGN2");
        event.setTicketId("TICKET2");
        event.setAgentId("AGENT1");
        event.setEventType("BREACHED");
        event.setOccurredAt(LocalDateTime.now());
        event.setRemarks("Resolution time breached");

        when(eventRepository.findByAgentId("AGENT1"))
                .thenReturn(List.of(event));

        List<SlaEventResponse> result =
                slaEventService.getEventsByAgent("AGENT1");

        assertEquals(1, result.size());

        SlaEventResponse response = result.get(0);
        assertEquals("ASSIGN2", response.getAssignmentId());
        assertEquals("TICKET2", response.getTicketId());
        assertEquals("AGENT1", response.getAgentId());
        assertEquals("BREACHED", response.getEventType());
        assertEquals("Resolution time breached", response.getRemarks());
    }


    @Test
    void getEventsByAgent_emptyResult() {

        when(eventRepository.findByAgentId("AGENT_X"))
                .thenReturn(List.of());

        List<SlaEventResponse> result =
                slaEventService.getEventsByAgent("AGENT_X");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
