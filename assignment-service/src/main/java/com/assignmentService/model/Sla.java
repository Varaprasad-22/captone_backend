package com.assignmentService.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "sla")
@Data
public class Sla {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long slaId;

	private String assignmentId; 
	private String ticketId;

	private LocalDateTime responseDeadline;
	private LocalDateTime resolutionDeadline;

	private LocalDateTime respondedAt;
	private LocalDateTime resolvedAt;

	private boolean escalated;
	private boolean breached;

	private LocalDateTime createdAt;
}
