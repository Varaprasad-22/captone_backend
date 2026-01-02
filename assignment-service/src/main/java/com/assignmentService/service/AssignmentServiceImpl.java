package com.assignmentService.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assignmentService.client.AuthClient;
import com.assignmentService.dto.AgentStatusCount;
import com.assignmentService.dto.AgentWorkLoadResponse;
import com.assignmentService.dto.AssignmentRequest;
import com.assignmentService.dto.NotificationEvent;
import com.assignmentService.dto.UserInfoResponse;
import com.assignmentService.model.Assignment;
import com.assignmentService.model.SlaStatus;
import com.assignmentService.repositories.AssignmentRepository;

import jakarta.transaction.Transactional;

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

	@Transactional
	public String assign(AssignmentRequest req, String assignedBy) {

		Assignment assign = new Assignment();
		assign.setTicketId(req.getTicketId());
		assign.setAgentId(req.getAgentId());
		assign.setAssignedBy(assignedBy);
		assign.setAssignedAt(LocalDateTime.now());
		assign.setPriority(req.getPriority());
		assign.setStatus(SlaStatus.ACTIVE);
		assign.setAssignmentId(UUID.randomUUID().toString());

		Assignment saved = assignmentRepo.save(assign);

		slaService.createSla(saved);

		// get the email via feing client from auth db and send it to email
		UserInfoResponse agent = authClient.getUserById(req.getAgentId());

		NotificationEvent event = new NotificationEvent("ASSIGNMENT_CREATED", agent.getEmail(), 
				"New Ticket Assigned",
				"You have been assigned ticket " + saved.getTicketId());

		publisher.publish(event, "assignment.created");

		return assign.getAssignmentId();
	}

	@Override
	public List<AgentWorkLoadResponse> getAgentWorkload(String agentId) {
		// TODO Auto-generated method stub
		 return assignmentRepo.countByStatus(agentId)
		            .stream()
		            .map(r -> new AgentWorkLoadResponse(
		                    r[0].toString(),
		                    (Long) r[1]
		            ))
		            .toList();
	}

	@Override
	public List<AgentStatusCount> getAllAgentsWorkload() {
		  return assignmentRepo.getAllAgentWorkload()
		            .stream()
		            .map(row -> new AgentStatusCount(
		                    (String) row[0],          // agentId
		                    row[1].toString(),        // status
		                    (Long) row[2]             // count
		            ))
		            .toList();
	}
}
