package com.assignmentService.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.assignmentService.client.AuthClient;
import com.assignmentService.client.TicketClient;
import com.assignmentService.dto.NotificationEvent;
import com.assignmentService.dto.UpdateTicketStatusRequest;
import com.assignmentService.model.Assignment;
import com.assignmentService.model.Sla;
import com.assignmentService.model.SlaEvent;
import com.assignmentService.model.SlaStatus;
import com.assignmentService.repositories.AssignmentRepository;
import com.assignmentService.repositories.SlaEventRepository;
import com.assignmentService.repositories.SlaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class SlaSchedular {

	private final SlaRepository slaRepo;
	private final AssignmentRepository assignmentRepo;
	private final NotificationPublisher publisher;
	private final SlaEventRepository slaEventRepository;
	private final TicketClient ticketClient;
	private final AuthClient authClient;
//	runs every 60 secs timer
	// see this one aims so if not responded within response time it shows of
	// escalation
	// if not resolved with in time it shows breached
	@Scheduled(fixedRate = 60000000)
	public void checkBreaches() {

		LocalDateTime now = LocalDateTime.now();

		for (Sla sla : slaRepo.findActiveSlas()) {

			boolean updated = false;
			Assignment assignment = assignmentRepo.findById(sla.getAssignmentId())
					.orElseThrow(() -> new RuntimeException("Failed to find assignment"));
			if (assignment.getStatus() != SlaStatus.CLOSED) {

//            see it checks only for escalations
//            like if not escalated already 
//            no response,response crosssed
				if (!sla.isEscalated() && sla.getRespondedAt() == null && now.isAfter(sla.getResponseDeadline())) {
					 try {
					String managerId = assignment.getAssignedBy();
					String managerEmail = authClient.getByUserId(managerId).getEmail();
					sla.setEscalated(true);

					updated = true;

					updateAssignment(sla.getAssignmentId(), SlaStatus.ESCALATED);
					slaEventRepository.save(new SlaEvent(null, sla.getAssignmentId(), sla.getTicketId(),
							assignment.getAgentId(), "ESCALATED", LocalDateTime.now(), "Response SLA crossed"));
					ticketClient.updateTicketStatus(sla.getTicketId(),
							new UpdateTicketStatusRequest(SlaStatus.ESCALATED));

					//getting notified for each things email 
					publisher.publish(new NotificationEvent("SLA_ESCALATED", managerEmail,
							"SLA Escalation", "Ticket " + sla.getTicketId() + " SLA escalated"), "sla.escalated");
					slaRepo.save(sla);
					 } catch (Exception e) {
                         System.err.println(
                                 "Escalation failed for ticket "
                                         + sla.getTicketId()
                                         + " : "
                                         + e.getMessage()
                         );
                     }
				}

//            breach checks like not resolved and dead line crossed even if escalaton true also
				if (sla.getResolvedAt() == null && now.isAfter(sla.getResolutionDeadline())) {

					   try {
					String managerId = assignment.getAssignedBy();
					String managerEmail = authClient.getByUserId(managerId).getEmail();
					sla.setBreached(true);
					updated = true;

					updateAssignment(sla.getAssignmentId(), SlaStatus.BREACHED);
					// see this is for logs

					slaEventRepository.save(new SlaEvent(null, sla.getAssignmentId(), sla.getTicketId(),
							assignment.getAgentId(), "ESCALATED", LocalDateTime.now(), "Response SLA crossed"));

					// this is for updating in ticket service
					//getting notified for each things email 
					publisher.publish(new NotificationEvent("SLA_BREACHED", managerEmail,
							"SLA Breached", "Ticket " + sla.getTicketId() + " SLA Breached"), "sla.breached");

					ticketClient.updateTicketStatus(sla.getTicketId(),
							new UpdateTicketStatusRequest(SlaStatus.BREACHED));
					slaRepo.save(sla);
					   } catch (Exception e) {
                           System.err.println(
                                   "Breach handling failed for ticket "
                                           + sla.getTicketId()
                                           + " : "
                                           + e.getMessage()
                           );
                       }
				}

//            prevent unnecesary db writes per minute
				
			}
		}
	}

	private void updateAssignment(String assignId, SlaStatus status) {
		Assignment assignment = assignmentRepo.findById(assignId)
				.orElseThrow(() -> new RuntimeException("Failed to find assignment"));
		assignment.setStatus(status);
		assignmentRepo.save(assignment);

	}
}