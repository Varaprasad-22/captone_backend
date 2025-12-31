package com.ticker_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ticker_service.dto.CreateTicketRequest;
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
			 @RequestPart("ticket") @Valid CreateTicketRequest request,
			@RequestPart(value = "files", required = false) List<MultipartFile> files) {
		String ticketId = ticketService.createTicket(request,files, userId);
		return ResponseEntity.status(HttpStatus.CREATED).body(ticketId);
	}
}
 