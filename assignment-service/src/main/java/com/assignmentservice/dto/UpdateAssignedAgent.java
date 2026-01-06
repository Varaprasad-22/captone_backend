package com.assignmentservice.dto;

import com.assignmentservice.model.Priority;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateAssignedAgent {
	
    @NotBlank(message = "Agent ID is mandatory")
	private String agentId;
	
	private Priority priority;
}
