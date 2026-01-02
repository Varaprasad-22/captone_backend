package com.assignmentService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReAssignment {

	@NotBlank
	private String ticketId;
	
	@NotBlank
	private String newAgentId;
}
