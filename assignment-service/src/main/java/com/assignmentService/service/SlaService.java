package com.assignmentService.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.assignmentService.model.Assignment;
import com.assignmentService.model.Sla;
import com.assignmentService.model.SlaRule;
import com.assignmentService.repositories.SlaRepository;
import com.assignmentService.repositories.SlaRuleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlaService {

	private final SlaRepository slaRepo;
	private final SlaRuleRepository ruleRepo;

	public void createSla(Assignment assignment) {

		SlaRule rule = ruleRepo.findByPriorityAndActiveTrue(assignment.getPriority())
				.orElseThrow(() -> new RuntimeException("SLA rule missing"));

		Sla sla = new Sla();
		sla.setAssignmentId(assignment.getAssignmentId());
		sla.setTicketId(assignment.getTicketId());
		sla.setResponseDeadline(assignment.getAssignedAt().plusMinutes(rule.getResponseMinutes()));
		sla.setResolutionDeadline(assignment.getAssignedAt().plusHours(rule.getResolutionHours()));
		sla.setCreatedAt(LocalDateTime.now());
		sla.setEscalated(false);
		sla.setBreached(false);

		slaRepo.save(sla);
	}
}
