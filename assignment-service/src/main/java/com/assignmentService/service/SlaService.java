package com.assignmentService.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.assignmentService.model.Assignment;
import com.assignmentService.model.Sla;
import com.assignmentService.model.SlaEvent;
import com.assignmentService.model.SlaRule;
import com.assignmentService.model.TicketStatus;
import com.assignmentService.repositories.AssignmentRepository;
import com.assignmentService.repositories.SlaEventRepository;
import com.assignmentService.repositories.SlaRepository;
import com.assignmentService.repositories.SlaRuleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlaService {

	private final SlaRepository slaRepo;
	private final SlaRuleRepository ruleRepo;
	private final AssignmentRepository assignmentRepo;
	private final SlaEventRepository slaEventRepository;

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

	@Transactional
	public void updateFromTicketStatus(String ticketId, TicketStatus status) {

		Assignment assignment = assignmentRepo.findByTicketId(ticketId)
				.orElseThrow(() -> new RuntimeException("assignment not found"));

		Sla sla = slaRepo.findByAssignmentId(assignment.getAssignmentId())
				.orElseThrow(() -> new RuntimeException("SLA not found"));

		LocalDateTime now = LocalDateTime.now();

		switch (status) {

		case INPROGRESS:
			if (sla.getRespondedAt() == null) {
				sla.setRespondedAt(now);

				slaEventRepository.save(new SlaEvent(null, sla.getAssignmentId(), sla.getTicketId(),
						assignment.getAgentId(), "RESPONDED", LocalDateTime.now(), "Responsed to query"));

			}
			break;

		case RESOLVED:
		case CLOSED:
			if (sla.getResolvedAt() == null) {
				sla.setResolvedAt(now);
				slaEventRepository.save(new SlaEvent(null, sla.getAssignmentId(), sla.getTicketId(),
						assignment.getAgentId(), "RESOLVED", LocalDateTime.now(), "Query Resolved"));
			}
			break;

		default:
			// do nothing for other statuses
			return;
		}

		slaRepo.save(sla);
	}

}
