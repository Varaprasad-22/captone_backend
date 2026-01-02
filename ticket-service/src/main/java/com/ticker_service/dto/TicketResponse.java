package com.ticker_service.dto;

import java.time.LocalDateTime;

import com.ticker_service.model.TicketCategory;
import com.ticker_service.model.TicketStatus;

import lombok.Data;

@Data
public class TicketResponse {
	private String ticketId;

	private String title;

	private String description;

	private TicketCategory category;

	private String priority;

	private TicketStatus status;

	private String createdByUserId;

	private String assignedAgentId;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
