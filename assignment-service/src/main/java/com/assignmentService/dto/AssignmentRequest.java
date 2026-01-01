package com.assignmentService.dto;

import com.assignmentService.model.Priority;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentRequest {

	@NotBlank
	private String ticketId;

	@NotBlank
	private String agentId;

    private Priority priority; // HIGH / MEDIUM / LOW
}
