package com.assignmentService.dto;

import com.assignmentService.model.Priority;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateAssignedAgent {
	private String agentId;
	
	private Priority priority;
}
