package com.ticker_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ticker_service.dto.TicketStatusUpdateRequest;
import com.ticker_service.model.TicketStatus;

@FeignClient(name = "assignment-service", url = "http://localhost:9094")
public interface AssignmentClient {

	@PutMapping("/assignments/internal/sla/update-from-ticket")
	void updateSlaFromTicket(@RequestBody  TicketStatusUpdateRequest request);
}
