package com.assignmentService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.assignmentService.dto.UpdateAssignedAgent;
import com.assignmentService.dto.UpdateTicketStatusRequest;

import jakarta.validation.Valid;

@FeignClient(name = "ticket-service", url = "http://localhost:9093")
public interface TicketClient {

	@PutMapping("/tickets/{ticketId}/status")
	void updateTicketStatus(@PathVariable String ticketId, @RequestBody UpdateTicketStatusRequest request);

	@PutMapping("/{ticketId}/updateAgentId")
	void updateUserId(@PathVariable String ticketId, @Valid @RequestBody UpdateAssignedAgent request);
}
