package com.ticker_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.ticker_service.dto.AddCommentRequest;
import com.ticker_service.dto.CreateTicketRequest;
import com.ticker_service.dto.TicketResponse;
import com.ticker_service.dto.UpdateAssignedAgent;
import com.ticker_service.dto.UpdateTicketStatusRequest;
import com.ticker_service.service.TickerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tickets")
public class TicketController {

	@Autowired
	private TickerService ticketService;

	// this is one for creating ticket where we send userid from token or other ways
	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createTicket(@RequestHeader("User-Id") String userId,
			@RequestHeader("EmailId") String userEmail, @RequestPart("ticket") @Valid CreateTicketRequest request,
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

	@GetMapping("/{userId}/getTickets")
	public ResponseEntity<List<TicketResponse>> getPerUserTickets(@PathVariable String userId) {
		return ResponseEntity.ok().body(ticketService.getPerUserTickets(userId));
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

		return ResponseEntity.ok().build();
	}
}
