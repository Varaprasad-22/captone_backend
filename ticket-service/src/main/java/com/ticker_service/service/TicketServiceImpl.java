package com.ticker_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ticker_service.dto.CreateTicketRequest;
import com.ticker_service.dto.NotificationEvent;
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

		NotificationEvent event = new NotificationEvent("TICKET_CREATED", userEmail, "Ticket Created",
				"Ticket '" + ticket.getTitle() + "' has been created.");

		publisher.publish(event, "ticket.created");
		return "Ticket Created Succesfully" + saved.getTicketId();
	}

}
