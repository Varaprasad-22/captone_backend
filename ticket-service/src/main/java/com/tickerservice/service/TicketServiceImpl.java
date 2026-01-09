package com.tickerservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tickerservice.client.AssignmentClient;
import com.tickerservice.client.AuthClient;
import com.tickerservice.dto.AttachmentResponse;
import com.tickerservice.dto.CommentResponse;
import com.tickerservice.dto.CreateTicketRequest;
import com.tickerservice.dto.NotificationEvent;
import com.tickerservice.dto.TicketResponse;
import com.tickerservice.dto.TicketStatusUpdateRequest;
import com.tickerservice.dto.UpdateAssignedAgent;
import com.tickerservice.dto.UserDashboardResponse;
import com.tickerservice.dto.UserInfoResponse;
import com.tickerservice.exceptions.InvalidTicketstateException;
import com.tickerservice.exceptions.TicketNotFoundException;
import com.tickerservice.model.Attachment;
import com.tickerservice.model.Comment;
import com.tickerservice.model.Priority;
import com.tickerservice.model.Ticket;
import com.tickerservice.model.TicketStatus;
import com.tickerservice.repository.AttachmentRepository;
import com.tickerservice.repository.CommentRepository;
import com.tickerservice.repository.TicketRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TickerService {

	private final TicketRepository ticketRepository;
	
	private final FileStorageService fileStorageService;
	
	private final AttachmentRepository attachmentRepository;

	private final NotificationPublisher publisher;

	private final AssignmentClient assignmentClient;
	
	private final CommentRepository commentRepo;

	private final AuthClient authClient;

	@Override
	@CircuitBreaker( name = "ticket-service", fallbackMethod = "createTicketFallback")
	public String createTicket(@Valid CreateTicketRequest request, List<MultipartFile> files, String userId,
			String userEmail) {
		Ticket ticket = new Ticket();
		ticket.setTitle(request.getTitle());
		ticket.setDescription(request.getDescription());
		ticket.setCategory(request.getCategory());
		ticket.setPriority(Priority.LOW);
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

	public String createTicketFallback(CreateTicketRequest request, List<MultipartFile> files, String userId,
	        String userEmail,Throwable ex) {
	    throw new RuntimeException("Ticket service temporarily unavailable. Please try again later.");
	}
	@Override
	@Transactional
	public void updateStatus(String ticketId, TicketStatus status) {
		
		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

		if(!TicketStatus.contains(status)) {
			throw new InvalidTicketstateException("see State is not listed");
		}
		ticket.setStatus(status);
		ticket.setUpdatedAt(LocalDateTime.now());

		ticketRepository.save(ticket);
		UserInfoResponse userdetails=authClient.getByUserId(ticket.getCreatedByUserId());
		String userEmail=userdetails.getEmail();
		

		//if escalated manager gets notified and user gets notifications too on each change of status
		if(status==TicketStatus.ESCALATED) {
			String managerId=assignmentClient.getManagerId(ticketId).getBody();
			UserInfoResponse managerDetails=authClient.getByUserId(managerId);
			String managerEmail=managerDetails.getEmail();
			
			NotificationEvent event = new NotificationEvent("TICKET_ESCALATED", managerEmail, "Ticket escalated", "Ticket '"
					+ ticket.getTitle() + "' has escalated of ticketId " + ticketId);

			publisher.publish(event, "ticket.Updates");
		}
		NotificationEvent event = new NotificationEvent("TICKET_STATUS_UPDATED", userEmail, "Ticket status update", "Ticket '"
				+ ticket.getTitle() + "' has updated the status." + "\n of ticket id " + ticketId);

		publisher.publish(event, "ticket.Updates");
		if (status != TicketStatus.ASSIGNED) {
		    assignmentClient.updateSlaFromTicket(
		        new TicketStatusUpdateRequest(ticketId, status)
		    );
		}

	}

	@Override
	public List<TicketResponse> getAllOpenTickets() {

		return ticketRepository.findByStatus(TicketStatus.OPEN);
	}

	@Override
	public List<TicketResponse> getPerUserTickets(String userId) {

		return ticketRepository.findByCreatedByUserId(userId);
	}

	@Override
	@CircuitBreaker(name = "ticket-service",fallbackMethod = "getAllTicketsFallback")
	public List<TicketResponse> getAllTickets() {

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
	public List<TicketResponse> getAllTicketsFallback(Throwable ex) {
	    return List.of();
	}
	@Override
	public void updateAgentId(String ticketId, @Valid UpdateAssignedAgent request) {

		Ticket existingTicket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
		existingTicket.setAssignedAgentId(request.getAgentId());
		existingTicket.setUpdatedAt(LocalDateTime.now());
		existingTicket.setStatus(TicketStatus.ASSIGNED);
		existingTicket.setPriority(request.getPriority());
		ticketRepository.save(existingTicket);
	}

	@Override
	public void addComment(String ticketId, String authorId, String text, boolean internal) {
	
		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

		Comment comment = new Comment();
		comment.setCommentId(UUID.randomUUID().toString());
		comment.setAuthorId(authorId);
		comment.setText(text);
		comment.setInternal(internal);
		comment.setCreatedAt(LocalDateTime.now());
		comment.setTicketId(ticket.getTicketId());
		commentRepo.save(comment);

	}

	@Override
	@CircuitBreaker(name = "ticket-service",fallbackMethod = "getCommentsFallback")
	public List<CommentResponse> getComments(String ticketId) {
	
		return commentRepo.findAllByTicketId(ticketId).stream().map(comment->{
				CommentResponse response=new CommentResponse();
				response.setAuthorId(comment.getAuthorId());
				response.setCommentId(comment.getCommentId());
				response.setCreatedAt(comment.getCreatedAt());
				response.setInternal(comment.isInternal());
				response.setText(comment.getText());
				response.setTicketId(comment.getTicketId());
				return response;
				}
				).toList();
	}
	public List<CommentResponse> getCommentsFallback(String ticketId, Throwable ex) {
	    return List.of(); 
	}

	@Override
	public TicketResponse viewTicket(String ticketId) {

		return ticketRepository.findById(ticketId).map(ticket -> {
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
		}).orElse(null);
	}
	
	  public List<AttachmentResponse> getAttachmentsByTicketId(String ticketId) {
	        return attachmentRepository.findByTicketId(ticketId).stream()
	                .map(attachment -> {
	                    AttachmentResponse response = new AttachmentResponse();
	                    response.setId(attachment.getId());
	                    response.setFileName(attachment.getFileName());
	                    response.setFileType(attachment.getFileType());
	                    response.setFileUrl(attachment.getFileUrl());
	                    response.setTicketId(attachment.getTicketId());
	                    response.setUploadedBy(attachment.getUploadedBy());
	                    response.setUploadedAt(attachment.getUploadedAt());
	                    return response;
	                })
	                .toList();
	    }


	  public AttachmentResponse getAttachmentById(String id) {
		    return attachmentRepository.findById(id)
		            .map(attachment -> {
		                AttachmentResponse response = new AttachmentResponse();
		                response.setId(attachment.getId());
		                response.setFileName(attachment.getFileName());
		                response.setFileType(attachment.getFileType());
		                response.setFileUrl(attachment.getFileUrl());
		                response.setTicketId(attachment.getTicketId());
		                response.setUploadedBy(attachment.getUploadedBy());
		                response.setUploadedAt(attachment.getUploadedAt());
		                return response;
		            })
		            .orElseThrow(() -> new RuntimeException("Attachment not found"));
		}

	  @Override
	  public List<TicketResponse> getAgentAllotedTickets(String agentId) {
	
		  return ticketRepository.findByAssignedAgentId(agentId);
	  }
	  public List<TicketResponse> getAgentResolvedTickets(String agentId) {
		    List<TicketStatus> resolvedStatuses = Arrays.asList(TicketStatus.RESOLVED, TicketStatus.CLOSED);

		    List<TicketResponse> tickets = ticketRepository.findByAssignedAgentIdAndStatusIn(agentId, resolvedStatuses);

		    return tickets;
	  }

	  //for the user dashboards
	  @Override
	    public UserDashboardResponse getUserDashboard(String userId) {

		  long total = ticketRepository.countByCreatedByUserId(userId);

		  long open = ticketRepository.countByCreatedByUserIdAndStatus(
		          userId, TicketStatus.OPEN
		  );

		  long inProgress = ticketRepository.countByCreatedByUserIdAndStatus(
		          userId, TicketStatus.INPROGRESS
		  );

		  long assigned = ticketRepository.countByCreatedByUserIdAndAssignedAgentIdIsNotNull(
		          userId
		  );

		  long closed = ticketRepository.countByCreatedByUserIdAndStatusIn(
		          userId,
		          List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED)
		  );


	        return new UserDashboardResponse(
	                total,
	                open,
	                inProgress,
	                assigned,
	                closed
	        );
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
