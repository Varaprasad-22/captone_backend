package com.tickerservice.dto;

import java.time.LocalDateTime;

import com.tickerservice.model.Priority;
import com.tickerservice.model.TicketCategory;
import com.tickerservice.model.TicketStatus;

import lombok.Data;

@Data
public class TicketResponse {
	private String ticketId;

	private String title;

	private String description;

	private TicketCategory category;

	private Priority priority;

	private TicketStatus status;

	private String createdByUserId;

	private String assignedAgentId;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
