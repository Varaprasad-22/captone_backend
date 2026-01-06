package com.assignmentService.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.assignmentservice.client.AuthClient;
import com.assignmentservice.client.TicketClient;
import com.assignmentservice.dto.NotificationEvent;
import com.assignmentservice.dto.UserInfoResponse;
import com.assignmentservice.model.Assignment;
import com.assignmentservice.model.Sla;
import com.assignmentservice.model.SlaStatus;
import com.assignmentservice.repositories.AssignmentRepository;
import com.assignmentservice.repositories.SlaEventRepository;
import com.assignmentservice.repositories.SlaRepository;
import com.assignmentservice.service.NotificationPublisher;
import com.assignmentservice.service.SlaSchedular;

class SlaSchedulerTest {

    @InjectMocks
    private SlaSchedular slaScheduler;

    @Mock
    private SlaRepository slaRepo;

    @Mock
    private AssignmentRepository assignmentRepo;

    @Mock
    private SlaEventRepository slaEventRepository;

    @Mock
    private AuthClient authClient;

    @Mock
    private TicketClient ticketClient;

    @Mock
    private NotificationPublisher publisher;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkBreaches_shouldPublishEscalationNotification() {

        Sla sla = new Sla();
        sla.setAssignmentId("A1");
        sla.setTicketId("T1");
        sla.setEscalated(false);
        sla.setRespondedAt(null);

        // 🔴 THIS IS THE FIX
        sla.setResponseDeadline(LocalDateTime.now().minusMinutes(10));
        sla.setResolutionDeadline(LocalDateTime.now().plusHours(1));

        Assignment assignment = new Assignment();
        assignment.setAssignmentId("A1");
        assignment.setTicketId("T1");
        assignment.setAgentId("AGENT1");
        assignment.setAssignedBy("MANAGER1");
        assignment.setStatus(SlaStatus.ACTIVE);

        when(slaRepo.findActiveSlas())
                .thenReturn(new Sla[]{ sla });

        when(assignmentRepo.findById("A1"))
                .thenReturn(Optional.of(assignment));

        UserInfoResponse manager =
                new UserInfoResponse("MANAGER1", "manager@test.com",
                        "Manager", true, "ROLE_MANAGER");

        when(authClient.getByUserId("MANAGER1"))
                .thenReturn(manager);

        slaScheduler.checkBreaches();

        verify(publisher).publish(
                any(NotificationEvent.class),
                eq("sla.escalated")
        );
    }

}
