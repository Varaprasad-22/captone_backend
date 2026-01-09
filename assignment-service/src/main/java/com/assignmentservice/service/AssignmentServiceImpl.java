package com.assignmentservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.assignmentservice.client.AuthClient;
import com.assignmentservice.client.TicketClient;
import com.assignmentservice.dto.AgentStatusCount;
import com.assignmentservice.dto.AgentWorkLoadResponse;
import com.assignmentservice.dto.AssignmentRequest;
import com.assignmentservice.dto.NotificationEvent;
import com.assignmentservice.dto.ReAssignment;
import com.assignmentservice.dto.UpdateAssignedAgent;
import com.assignmentservice.dto.UpdateTicketStatusRequest;
import com.assignmentservice.dto.UserInfoResponse;
import com.assignmentservice.exception.AgentUnavailableException;
import com.assignmentservice.exception.AssignmentAlreadyExistsException;
import com.assignmentservice.exception.AssignmentNotFoundException;
import com.assignmentservice.model.Assignment;
import com.assignmentservice.model.Priority;
import com.assignmentservice.model.SlaStatus;
import com.assignmentservice.repositories.AssignmentRepository;
import com.assignmentservice.repositories.SlaRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

	private final AssignmentRepository assignmentRepo;

	private final SlaService slaService;

	private final AuthClient authClient;

	private final NotificationPublisher publisher;

	private final TicketClient ticketClient;

	private final SlaRepository slaRepo;

	@CircuitBreaker(name = "assignment-service",fallbackMethod = "assignFallback")
	@Transactional
	public String assign(AssignmentRequest req, String assignedBy) {

		// okay if we retry to assign same ticket to others it show errror
		assignmentRepo.findByTicketId(req.getTicketId()).ifPresent(a -> {
			throw new AssignmentAlreadyExistsException("Ticket is already assigned to an agent");
		});

		UserInfoResponse agent = authClient.getByUserId(req.getAgentId());

		if (agent == null || !agent.isActive()) {
			throw new AgentUnavailableException("Selected agent is not available");
		}

		Assignment assign = new Assignment();
		assign.setTicketId(req.getTicketId());
		assign.setAgentId(req.getAgentId());
		assign.setAssignedBy(assignedBy);
		assign.setAssignedAt(LocalDateTime.now());
		assign.setPriority(req.getPriority() != null ? req.getPriority() : Priority.LOW);
		assign.setStatus(SlaStatus.ACTIVE);
		assign.setAssignmentId(UUID.randomUUID().toString());

		Assignment saved = assignmentRepo.save(assign);
		ticketClient.updateUserId(saved.getTicketId(),
				new UpdateAssignedAgent(saved.getAgentId(), saved.getPriority()));
		slaService.createSla(saved);

		// get the email via feing client from auth db and send it to email


		NotificationEvent event = new NotificationEvent("ASSIGNMENT_CREATED", agent.getEmail(), "New Ticket Assigned",
				"You have been assigned ticket " + saved.getTicketId());

		publisher.publish(event, "assignment.created");

		ticketClient.updateTicketStatus(req.getTicketId(), new UpdateTicketStatusRequest(SlaStatus.ASSIGNED));

		return assign.getAssignmentId();
	}
	public String assignFallback(AssignmentRequest req,String assignedBy,Throwable ex	) {
	    throw new RuntimeException(
	        "Assignment service temporarily unavailable. Please try again later."
	    );
	}
	@Override
	public List<AgentWorkLoadResponse> getAgentWorkload(String agentId) {

		return assignmentRepo.countByStatus(agentId).stream()
				.map(r -> new AgentWorkLoadResponse(r[0].toString(), (Long) r[1])).toList();
	}

	@Override
	public List<AgentStatusCount> getAllAgentsWorkload() {
		return assignmentRepo.getAllAgentWorkload().stream().map(row -> new AgentStatusCount((String) row[0], // agentId
				row[1].toString(), // status
				(Long) row[2] // count
		)).toList();
	}

	@CircuitBreaker(name = "assignment-service",fallbackMethod = "reassignFallback")
	@Override
	@Transactional
	public String reassign(String assignedBy, ReAssignment request) {

		Assignment oldAssignment = assignmentRepo.findAllByTicketIdOrderByAssignedAtDesc(request.getTicketId())
		        .stream()
		        .findFirst()
		        .orElseThrow(() -> new AssignmentNotFoundException("No Ticket Found"));
		oldAssignment.setStatus(SlaStatus.REASSIGNED);
		assignmentRepo.save(oldAssignment);

//	    Sla oldSla=slaRepo.findByAssignmentId(oldAssignment.getAssignmentId()).orElseThrow(()-> new RuntimeException("No record found"));
//	    oldSla.set

		Assignment newAssignment = new Assignment();
		newAssignment.setAssignmentId(UUID.randomUUID().toString());
		newAssignment.setTicketId(request.getTicketId());
		newAssignment.setAgentId(request.getNewAgentId());
		newAssignment.setAssignedBy(assignedBy);
		newAssignment.setAssignedAt(LocalDateTime.now());
		newAssignment.setPriority(oldAssignment.getPriority());
		newAssignment.setStatus(SlaStatus.ACTIVE);
		Assignment saved = assignmentRepo.save(newAssignment);
		ticketClient.updateUserId(saved.getTicketId(),
				new UpdateAssignedAgent(saved.getAgentId(), saved.getPriority()));
		slaService.createSla(newAssignment);
		return newAssignment.getAssignmentId();
	}
	public String reassignFallback(String assignedBy,ReAssignment request,Throwable ex) {
	    throw new RuntimeException(
	        "Reassignment failed temporarily. Please try again later."
	    );
	}

	@Override
	public String getManagerId(String ticketId) {
	    Assignment assignment =assignmentRepo.findByTicketId(ticketId).orElseThrow(()->new AssignmentNotFoundException("No corresponding ticketId"));
		return assignment.getAssignedBy();
	}
}
