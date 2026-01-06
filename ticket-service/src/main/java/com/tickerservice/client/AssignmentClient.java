package com.tickerservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tickerservice.dto.TicketStatusUpdateRequest;

@FeignClient(name = "assignment-service", url = "http://localhost:9094")
public interface AssignmentClient {

	@PutMapping("/assignments/internal/sla/update-from-ticket")
	void updateSlaFromTicket(@RequestBody  TicketStatusUpdateRequest request);
	
	@GetMapping("/assignments/internal/{ticketId}/getManagerId")
	public ResponseEntity<String> getManagerId(@PathVariable String ticketId);
}
