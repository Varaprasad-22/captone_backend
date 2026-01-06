package com.notificationservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long notificationId;

	// see this thing stores all details of for what perpose or from which service
	// like if userCreated ,Tickets, assignments things
	// USER_CREATED, TICKET_CREATED, ASSIGNED, SLA_ESCALATED
	private String eventType;

	private String recipientEmail;

	private String subject;

	@Column(columnDefinition = "TEXT")
	private String message;

	// it is for defining the failed or sent things 
	private String status;

	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	private LocalDateTime createdAt;

}
