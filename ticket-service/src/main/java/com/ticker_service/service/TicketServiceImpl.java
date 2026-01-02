package com.ticker_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ticker_service.client.AssignmentClient;
import com.ticker_service.dto.CreateTicketRequest;
import com.ticker_service.dto.NotificationEvent;
import com.ticker_service.dto.TicketResponse;
import com.ticker_service.dto.TicketStatusUpdateRequest;
import com.ticker_service.model.Attachment;
import com.ticker_service.model.Ticket;
import com.ticker_service.model.TicketStatus;
import com.ticker_service.repository.AttachmentRepository;
import com.ticker_service.repository.TicketRepository;

import jakarta.validation.Valid;

@Service
public class TicketServiceImpl implements TickerService {

	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private FileStorageService fileStorageService;
	@Autowired
	private AttachmentRepository attachmentRepository;
	@Autowired
	private NotificationPublisher publisher;
	@Autowired
	private AssignmentClient assignmentClient;

	@Override
	public String createTicket(@Valid CreateTicketRequest request, List<MultipartFile> files, String userId,
			String userEmail) {
		Ticket ticket = new Ticket();
		ticket.setTitle(request.getTitle());
		ticket.setDescription(request.getDescription());
		ticket.setCategory(request.getCategory());
		ticket.setPriority("Low");
		ticket.setStatus(TicketStatus.OPEN);
		ticket.setCreatedByUserId(userId);
		ticket.setCreatedAt(LocalDateTime.now());
		ticket.setUpdatedAt(LocalDateTime.now());
		Ticket saved = ticketRepository.save(ticket);

		// trying to save files if not null
		// more over I will be saving them like in local system
		List<Attachment> attachments = new ArrayList<>();
		if (files != null) {
			for (MultipartFile file : files) {
				if (file.isEmpty())
					continue;
				String path = fileStorageService.save(file);
				Attachment attach = new Attachment();
				attach.setFileName(file.getOriginalFilename());
				attach.setTicketId(saved.getTicketId());
				attach.setFileType(file.getContentType());
				attach.setFileUrl(path);
				attach.setUploadedBy(userId);
				attach.setUploadedAt(LocalDateTime.now());
				attachments.add(attach);
			}
			if (!attachments.isEmpty())
				attachmentRepository.saveAll(attachments);
		}

		NotificationEvent event = new NotificationEvent("TICKET_CREATED", userEmail, "Ticket Created", "Ticket '"
				+ ticket.getTitle() + "' has been created." + "\n With the ticket id as " + saved.getTicketId());

		publisher.publish(event, "ticket.created");
		return "Ticket Created Succesfully" + saved.getTicketId();
	}

	@Override
	public void updateStatus(String ticketId, TicketStatus status) {
		// TODO Auto-generated method stub
		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));

		ticket.setStatus(status);
		ticket.setUpdatedAt(LocalDateTime.now());

		ticketRepository.save(ticket);
		if (TicketStatus.ASSIGNED != status)
			assignmentClient.updateSlaFromTicket(new TicketStatusUpdateRequest(ticketId, status));
	}

	@Override
	public List<TicketResponse> getAllOpenTickets() {
		// TODO Auto-generated method stub
		return ticketRepository.findByStatus(TicketStatus.OPEN);
	}

	@Override
	public List<TicketResponse> getPerUserTickets(String userId) {
		// TODO Auto-generated method stub
		return ticketRepository.findByCreatedByUserId(userId);
	}

	@Override
	public List<TicketResponse> getAllTickets() {
		// TODO Auto-generated method stub
		return ticketRepository.findAll().stream().map(ticket -> {
			TicketResponse response = new TicketResponse();
			response.setTicketId(ticket.getTicketId());
			response.setTitle(ticket.getTitle());
			response.setDescription(ticket.getDescription());
			response.setStatus(ticket.getStatus());
			response.setPriority(ticket.getPriority());
			response.setCreatedAt(ticket.getCreatedAt());
			response.setCategory(ticket.getCategory());
			response.setCreatedByUserId(ticket.getCreatedByUserId());
			response.setAssignedAgentId(ticket.getAssignedAgentId());
			response.setCreatedAt(ticket.getCreatedAt());
			response.setUpdatedAt(ticket.getUpdatedAt());
			return response;
		}).toList();
	}

	// see since from db we get ticket we wanted ticket Response
//	private TicketResponse mapToResponse(Ticket	ticket) {
//		return TicketResponse.builder()
//				.ticketId(ticket.getTicketId())
//				 .title(ticket.getTitle())
//		         .description(ticket.getDescription())
//		         .status(ticket.getStatus())
//		         .priority(ticket.getPriority())
//		         .createdAt(ticket.getCreatedAt())
//		         .updatedAt(ticket.getUpdatedAt())
//		         .assignedAgentId(ticket.getAssignedAgentId())
//				.build();
//	}
}
