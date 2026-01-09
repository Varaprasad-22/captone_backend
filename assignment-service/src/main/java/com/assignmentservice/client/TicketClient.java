package com.assignmentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.assignmentservice.dto.UpdateAssignedAgent;
import com.assignmentservice.dto.UpdateTicketStatusRequest;

import jakarta.validation.Valid;

@FeignClient(name = "ticket-service", url = "${ticket.service.url}")
public interface TicketClient {

	@PutMapping("/tickets/{ticketId}/status")
	void updateTicketStatus(@PathVariable String ticketId, @RequestBody UpdateTicketStatusRequest request);

	@PutMapping("/tickets/{ticketId}/updateAgentId")
	void updateUserId(@PathVariable String ticketId, @Valid @RequestBody UpdateAssignedAgent request);
}
