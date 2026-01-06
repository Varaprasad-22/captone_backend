package com.assignmentService.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.assignmentService.model.Assignment;
import com.assignmentService.model.Priority;
import com.assignmentService.model.Sla;
import com.assignmentService.model.SlaEvent;
import com.assignmentService.model.SlaRule;
import com.assignmentService.model.TicketStatus;
import com.assignmentService.repositories.AssignmentRepository;
import com.assignmentService.repositories.SlaEventRepository;
import com.assignmentService.repositories.SlaRepository;
import com.assignmentService.repositories.SlaRuleRepository;

class SlaServiceTest {

    @Mock
    private SlaRepository slaRepo;

    @Mock
    private SlaRuleRepository ruleRepo;

    @Mock
    private AssignmentRepository assignmentRepo;

    @Mock
    private SlaEventRepository slaEventRepository;

    @InjectMocks
    private SlaService slaService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSla_success() {

        Assignment assignment = new Assignment();
        assignment.setAssignmentId("A1");
        assignment.setTicketId("T1");
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setPriority(Priority.HIGH);

        SlaRule rule = new SlaRule();
        rule.setResponseMinutes(30);
        rule.setResolutionHours(4);

        when(ruleRepo.findByPriorityAndActiveTrue(Priority.HIGH))
                .thenReturn(Optional.of(rule));

        slaService.createSla(assignment);

        verify(slaRepo).save(any(Sla.class));
    }

    @Test
    void createSla_ruleMissing_throwsException() {

        Assignment assignment = new Assignment();
        assignment.setPriority(Priority.LOW);

        when(ruleRepo.findByPriorityAndActiveTrue(Priority.LOW))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> slaService.createSla(assignment));
    }


    @Test
    void updateFromTicketStatus_inProgress_setsRespondedAtAndCreatesEvent() {

        Assignment assignment = new Assignment();
        assignment.setAssignmentId("A1");
        assignment.setTicketId("T1");
        assignment.setAgentId("AGENT1");

        Sla sla = new Sla();
        sla.setAssignmentId("A1");

        when(assignmentRepo.findByTicketId("T1"))
                .thenReturn(Optional.of(assignment));
        when(slaRepo.findByAssignmentId("A1"))
                .thenReturn(Optional.of(sla));

        slaService.updateFromTicketStatus("T1", TicketStatus.INPROGRESS);

        assertNotNull(sla.getRespondedAt());
        verify(slaEventRepository).save(any(SlaEvent.class));
        verify(slaRepo).save(sla);
    }

    @Test
    void updateFromTicketStatus_inProgress_alreadyResponded_noEvent() {

        Assignment assignment = new Assignment();
        assignment.setAssignmentId("A1");
        assignment.setTicketId("T1");
        assignment.setAgentId("AGENT1");

        Sla sla = new Sla();
        sla.setAssignmentId("A1");
        sla.setRespondedAt(LocalDateTime.now());

        when(assignmentRepo.findByTicketId("T1"))
                .thenReturn(Optional.of(assignment));
        when(slaRepo.findByAssignmentId("A1"))
                .thenReturn(Optional.of(sla));

        slaService.updateFromTicketStatus("T1", TicketStatus.INPROGRESS);

        verify(slaEventRepository, never()).save(any());
        verify(slaRepo).save(sla);
    }

    @Test
    void updateFromTicketStatus_resolved_setsResolvedAtAndCreatesEvent() {

        Assignment assignment = new Assignment();
        assignment.setAssignmentId("A1");
        assignment.setTicketId("T1");
        assignment.setAgentId("AGENT1");

        Sla sla = new Sla();
        sla.setAssignmentId("A1");

        when(assignmentRepo.findByTicketId("T1"))
                .thenReturn(Optional.of(assignment));
        when(slaRepo.findByAssignmentId("A1"))
                .thenReturn(Optional.of(sla));

        slaService.updateFromTicketStatus("T1", TicketStatus.RESOLVED);

        assertNotNull(sla.getResolvedAt());
        verify(slaEventRepository).save(any(SlaEvent.class));
        verify(slaRepo).save(sla);
    }


    @Test
    void updateFromTicketStatus_otherStatus_doesNothing() {

        Assignment assignment = new Assignment();
        assignment.setAssignmentId("A1");
        assignment.setTicketId("T1");

        Sla sla = new Sla();
        sla.setAssignmentId("A1");

        when(assignmentRepo.findByTicketId("T1"))
                .thenReturn(Optional.of(assignment));
        when(slaRepo.findByAssignmentId("A1"))
                .thenReturn(Optional.of(sla));

        slaService.updateFromTicketStatus("T1", TicketStatus.OPEN);

        verify(slaEventRepository, never()).save(any());
        verify(slaRepo, never()).save(any());
    }


    @Test
    void updateFromTicketStatus_assignmentNotFound_throwsException() {

        when(assignmentRepo.findByTicketId("T1"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> slaService.updateFromTicketStatus("T1", TicketStatus.INPROGRESS));
    }

    @Test
    void updateFromTicketStatus_slaNotFound_throwsException() {

        Assignment assignment = new Assignment();
        assignment.setAssignmentId("A1");

        when(assignmentRepo.findByTicketId("T1"))
                .thenReturn(Optional.of(assignment));
        when(slaRepo.findByAssignmentId("A1"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> slaService.updateFromTicketStatus("T1", TicketStatus.INPROGRESS));
    }
}
