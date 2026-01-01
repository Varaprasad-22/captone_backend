package com.assignmentService.dto;

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

	@NotBlank
	private String priority; // HIGH / MEDIUM / LOW
}
