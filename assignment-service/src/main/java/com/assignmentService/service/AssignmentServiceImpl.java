package com.assignmentService.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assignmentService.dto.AssignmentRequest;
import com.assignmentService.model.Assignment;
import com.assignmentService.repositories.AssignmentRepository;

import jakarta.transaction.Transactional;

@Service
public class AssignmentServiceImpl implements AssignmentService {

	@Autowired
	private AssignmentRepository assignmentRepo;
	@Autowired
	private SlaService slaService;

	@Transactional
	public String assign(AssignmentRequest req, String assignedBy) {
		
		Assignment assign = new Assignment();
		assign.setTicketId(req.getTicketId());
		assign.setAgentId(req.getAgentId());
		assign.setAssignedBy(assignedBy);
		assign.setAssignedAt(LocalDateTime.now());
		assign.setPriority(req.getPriority());
		assign.setStatus("ACTIVE");
		assign.setAssignmentId(UUID.randomUUID().toString());
		
		Assignment saved = assignmentRepo.save(assign);


        slaService.createSla(saved);
		
		return assign.getAssignmentId();
	}
}
