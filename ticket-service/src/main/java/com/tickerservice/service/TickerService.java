package com.tickerservice.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.tickerservice.dto.AttachmentResponse;
import com.tickerservice.dto.CommentResponse;
import com.tickerservice.dto.CreateTicketRequest;
import com.tickerservice.dto.TicketResponse;
import com.tickerservice.dto.UpdateAssignedAgent;
import com.tickerservice.dto.UserDashboardResponse;
import com.tickerservice.model.TicketStatus;

import jakarta.validation.Valid;

public interface TickerService {

	String createTicket(@Valid CreateTicketRequest request, List<MultipartFile> files, String userId, String userEmail);


    void updateStatus(String ticketId, TicketStatus status);


	List<TicketResponse> getAllOpenTickets();


	List<TicketResponse> getPerUserTickets(String userId);


	List<TicketResponse> getAllTickets();


	void updateAgentId(String ticketId, @Valid UpdateAssignedAgent request);


	void addComment(String ticketId, String authorId, String text, boolean internal);


	List<CommentResponse> getComments(String ticketId);


	TicketResponse viewTicket(String ticketId);


	List<AttachmentResponse> getAttachmentsByTicketId(String ticketId);


	AttachmentResponse getAttachmentById(String attachmentId);


	List<TicketResponse> getAgentAllotedTickets(String agentId);
	
	//for dashboards
    UserDashboardResponse getUserDashboard(String userId);


	List<TicketResponse> getAgentResolvedTickets(String agentId);
}
