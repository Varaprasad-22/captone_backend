package com.assignmentService.model;

import java.time.LocalDateTime;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "assignments")
public class Assignment {
	@Id
	private String assignmentId;

	private String ticketId;

	private String agentId;
	private String assignedBy;
	private LocalDateTime assignedAt;
	
    @Enumerated(EnumType.STRING)
    private Priority priority;

	
	
	private String status;
}
