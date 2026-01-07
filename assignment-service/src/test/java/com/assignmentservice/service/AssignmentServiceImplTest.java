package com.assignmentservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.assignmentservice.client.AuthClient;
import com.assignmentservice.client.TicketClient;
import com.assignmentservice.dto.*;
import com.assignmentservice.exception.*;
import com.assignmentservice.model.Assignment;
import com.assignmentservice.model.Priority;
import com.assignmentservice.repositories.AssignmentRepository;
import com.assignmentservice.repositories.SlaRepository;
import com.assignmentservice.service.AssignmentServiceImpl;
import com.assignmentservice.service.NotificationPublisher;
import com.assignmentservice.service.SlaService;

class AssignmentServiceImplTest {

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    @Mock
    private AssignmentRepository assignmentRepo;

    @Mock
    private SlaService slaService;

    @Mock
    private AuthClient authClient;

    @Mock
    private NotificationPublisher publisher;

    @Mock
    private TicketClient ticketClient;

    @Mock
    private SlaRepository slaRepo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void assign_success() {

        AssignmentRequest req = new AssignmentRequest();
        req.setTicketId("T1");
        req.setAgentId("A1");
        req.setPriority(Priority.HIGH);

        UserInfoResponse agent =
                new UserInfoResponse("A1", "agent@test.com", "Agent", true, "ROLE_AGENT");

        when(assignmentRepo.findByTicketId("T1")).thenReturn(Optional.empty());
        when(authClient.getByUserId("A1")).thenReturn(agent);
        when(assignmentRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        String assignmentId = assignmentService.assign(req, "MANAGER1");

        assertNotNull(assignmentId);

        verify(ticketClient).updateUserId(eq("T1"), any(UpdateAssignedAgent.class));
        verify(ticketClient).updateTicketStatus(eq("T1"), any(UpdateTicketStatusRequest.class));
        verify(slaService).createSla(any());
        verify(publisher).publish(any(NotificationEvent.class), eq("assignment.created"));
    }

    @Test
    void assign_ticketAlreadyAssigned_throwsException() {

        when(assignmentRepo.findByTicketId("T1"))
                .thenReturn(Optional.of(new Assignment()));

        AssignmentRequest req = new AssignmentRequest();
        req.setTicketId("T1");

        assertThrows(
                AssignmentAlreadyExistsException.class,
                () -> assignmentService.assign(req, "MANAGER")
        );
    }

    @Test
    void assign_agentUnavailable_throwsException() {

        AssignmentRequest req = new AssignmentRequest();
        req.setTicketId("T1");
        req.setAgentId("A1");

        when(assignmentRepo.findByTicketId("T1")).thenReturn(Optional.empty());
        when(authClient.getByUserId("A1"))
                .thenReturn(new UserInfoResponse("A1", "x@test.com", "Agent", false, "ROLE_AGENT"));

        assertThrows(
                AgentUnavailableException.class,
                () -> assignmentService.assign(req, "MANAGER")
        );
    }


    @Test
    void getAgentWorkload_success() {

    

    	when(assignmentRepo.countByStatus("A1"))
    	        .thenReturn(List.<Object[]>of(
    	    	        new Object[]{"ACTIVE", 3L}
    	            	));

        List<AgentWorkLoadResponse> result =
                assignmentService.getAgentWorkload("A1");

        assertEquals(1, result.size());
        assertEquals("ACTIVE", result.get(0).getStatus());
        assertEquals(3L, result.get(0).getCount());
    }

    @Test
    void getAllAgentsWorkload_success() {

    	when(assignmentRepo.getAllAgentWorkload())
        .thenReturn(List.<Object[]>of(
                new Object[]{"A1", "ACTIVE", 5L}
        ));

        List<AgentStatusCount> result =
                assignmentService.getAllAgentsWorkload();

        assertEquals(1, result.size());
        assertEquals("A1", result.get(0).getAgentId());
        assertEquals("ACTIVE", result.get(0).getStatus());
        assertEquals(5L, result.get(0).getCount());
    }


    @Test
    void reassign_success() {

        Assignment old = new Assignment();
        old.setAssignmentId("OLD");
        old.setTicketId("T1");
        old.setPriority(Priority.MEDIUM);
        old.setAssignedAt(LocalDateTime.now());

        when(assignmentRepo.findAllByTicketIdOrderByAssignedAtDesc("T1"))
                .thenReturn(List.of(old));

        when(assignmentRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        ReAssignment req = new ReAssignment();
        req.setTicketId("T1");
        req.setNewAgentId("A2");

        String newId = assignmentService.reassign("MANAGER", req);

        assertNotNull(newId);
        verify(slaService).createSla(any());
        verify(ticketClient).updateUserId(eq("T1"), any(UpdateAssignedAgent.class));
    }

    @Test
    void reassign_ticketNotFound_throwsException() {

        when(assignmentRepo.findAllByTicketIdOrderByAssignedAtDesc("T1"))
                .thenReturn(List.of());

        ReAssignment req = new ReAssignment();
        req.setTicketId("T1");

        assertThrows(
                AssignmentNotFoundException.class,
                () -> assignmentService.reassign("MANAGER", req)
        );
    }


    @Test
    void getManagerId_success() {

        Assignment a = new Assignment();
        a.setAssignedBy("MANAGER1");

        when(assignmentRepo.findByTicketId("T1"))
                .thenReturn(Optional.of(a));

        String managerId = assignmentService.getManagerId("T1");

        assertEquals("MANAGER1", managerId);
    }

    @Test
    void getManagerId_notFound_throwsException() {

        when(assignmentRepo.findByTicketId("T1"))
                .thenReturn(Optional.empty());

        assertThrows(
                AssignmentNotFoundException.class,
                () -> assignmentService.getManagerId("T1")
        );
    }
}
