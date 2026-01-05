package com.assignmentService.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assignmentService.client.AuthClient;
import com.assignmentService.client.TicketClient;
import com.assignmentService.dto.AgentStatusCount;
import com.assignmentService.dto.AgentWorkLoadResponse;
import com.assignmentService.dto.AssignmentRequest;
import com.assignmentService.dto.NotificationEvent;
import com.assignmentService.dto.ReAssignment;
import com.assignmentService.dto.UpdateAssignedAgent;
import com.assignmentService.dto.UpdateTicketStatusRequest;
import com.assignmentService.dto.UserInfoResponse;
import com.assignmentService.exception.AgentUnavailableException;
import com.assignmentService.exception.AssignmentAlreadyExistsException;
import com.assignmentService.exception.AssignmentNotFoundException;
import com.assignmentService.model.Assignment;
import com.assignmentService.model.Priority;
import com.assignmentService.model.Sla;
import com.assignmentService.model.SlaStatus;
import com.assignmentService.repositories.AssignmentRepository;
import com.assignmentService.repositories.SlaRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class AssignmentServiceImpl implements AssignmentService {

	@Autowired
	private AssignmentRepository assignmentRepo;
	@Autowired
	private SlaService slaService;
	@Autowired
	private AuthClient authClient;
	@Autowired
	private NotificationPublisher publisher;
	@Autowired
	private TicketClient ticketClient;
	@Autowired
	private SlaRepository slaRepo;

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

	@Override
	public List<AgentWorkLoadResponse> getAgentWorkload(String agentId) {
		// TODO Auto-generated method stub
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

	@Override
	@Transactional
	public String reassign(String assignedBy, ReAssignment request) {
		// TODO Auto-generated method stub
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

	@Override
	public String getManagerId(String ticketId) {
	    Assignment assignment =assignmentRepo.findByTicketId(ticketId).orElseThrow(()->new AssignmentNotFoundException("No corresponding ticketId"));
		return assignment.getAssignedBy();
	}
}
