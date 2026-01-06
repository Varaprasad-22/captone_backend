package com.tickerservice.controller;

//import java.net.http.HttpHeaders;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tickerservice.dto.AddCommentRequest;
import com.tickerservice.dto.AttachmentResponse;
import com.tickerservice.dto.CommentResponse;
import com.tickerservice.dto.CreateTicketRequest;
import com.tickerservice.dto.TicketResponse;
import com.tickerservice.dto.UpdateAssignedAgent;
import com.tickerservice.dto.UpdateTicketStatusRequest;
import com.tickerservice.dto.UserDashboardResponse;
import com.tickerservice.service.TickerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

	private final TickerService ticketService;

	// this is one for creating ticket where we send userid from token or other ways
	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createTicket(@RequestHeader("X-USER-ID") String userId,
			@RequestHeader("X-USER-EMAIL") String userEmail, @RequestPart("ticket") @Valid CreateTicketRequest request,
			@RequestPart(value = "files", required = false) List<MultipartFile> files) {
		String ticketId = ticketService.createTicket(request, files, userId, userEmail);
		return ResponseEntity.status(HttpStatus.CREATED).body(ticketId);
	}

	// see here we are trying to update the end point such that we can change the
	// status and get communicated to assignment db
	@PutMapping("/{ticketId}/status")
	public ResponseEntity<Void> updateTicketStatus(@PathVariable String ticketId,
			@Valid @RequestBody UpdateTicketStatusRequest request) {

		ticketService.updateStatus(ticketId, request.getStatus());
		return ResponseEntity.noContent().build();
	}

	// show the open tickets
	@GetMapping("/getAllOpenTickets")
	public ResponseEntity<List<TicketResponse>> getAllOpenTickets() {
		return ResponseEntity.ok().body(ticketService.getAllOpenTickets());
	}

	//for user tickets
	@GetMapping("/{userId}/getTickets")
	public ResponseEntity<List<TicketResponse>> getPerUserTickets(@PathVariable String userId) {
		return ResponseEntity.ok().body(ticketService.getPerUserTickets(userId));
	}

	//for allocated agent tickets
	@GetMapping("/{agentId}/getAgentTickets")
	public ResponseEntity<List<TicketResponse>> getAgentAllotedTickets(@PathVariable String agentId) {
		return ResponseEntity.ok().body(ticketService.getAgentAllotedTickets(agentId));
	}
	
	//get resolved tickets
	@GetMapping("/{agentId}/getAgentResolvedTickets")
	public ResponseEntity<List<TicketResponse>> getAgentResolvedTickets(@PathVariable String agentId) {
		return ResponseEntity.ok().body(ticketService.getAgentResolvedTickets(agentId));
	}
	@GetMapping("/getAllTickets")
	public ResponseEntity<List<TicketResponse>> getAllTickets() {
		return ResponseEntity.ok().body(ticketService.getAllTickets());
	}

	@PutMapping("/{ticketId}/updateAgentId")
	public ResponseEntity<Void> updateUserId(@PathVariable String ticketId,
			@Valid @RequestBody UpdateAssignedAgent request) {
		ticketService.updateAgentId(ticketId, request);
		return ResponseEntity.noContent().build();
	}

	// now for writing comments
	@PostMapping("/{ticketId}/comments")
	public ResponseEntity<Void> addComment(@PathVariable String ticketId, @RequestHeader("X-USER-ID") String authorId,
			@Valid @RequestBody AddCommentRequest request) {

		ticketService.addComment(ticketId, authorId, request.getText(), request.isInternal());

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@GetMapping("/{ticketId}/getComments")
	public ResponseEntity<List<CommentResponse>> getComments(@PathVariable String ticketId){
		return ResponseEntity.ok().body(ticketService.getComments(ticketId));
	}
	
	@GetMapping("/{ticketId}/getTicket")
	public ResponseEntity<TicketResponse> viewASpecificTicket(@PathVariable String ticketId) {
		return ResponseEntity.ok().body(ticketService.viewTicket(ticketId));
	}
	@GetMapping("/{ticketId}/attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(
			@PathVariable String ticketId) {

        return ResponseEntity.ok(
        		ticketService.getAttachmentsByTicketId(ticketId)
        );
    }
	
	@GetMapping("/attachments/view/{attachmentId}")
	public ResponseEntity<Resource> viewAttachment(@PathVariable String attachmentId)
	        throws Exception {

	    AttachmentResponse attachment = ticketService.getAttachmentById(attachmentId);

	    Path filePath = Paths.get(attachment.getFileUrl());
	    Resource resource = new UrlResource(filePath.toUri());

	    return ResponseEntity.ok()
	            .contentType(MediaType.parseMediaType(attachment.getFileType()))
	            .header(HttpHeaders.CONTENT_DISPOSITION,
	                    "inline; filename=\"" + attachment.getFileName() + "\"")
	            .body(resource);
	}
	
	
	//for the daashboards
	@GetMapping("/my/dashboard")
	public ResponseEntity<UserDashboardResponse> getUserDashboard(
	        @RequestHeader("X-USER-ID") String userId) {

	    return ResponseEntity.ok(
	            ticketService.getUserDashboard(userId)
	    );
	}
}
