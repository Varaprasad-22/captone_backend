package com.ticker_service.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "ticketCollection")
public class Ticket {

	@Id
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
