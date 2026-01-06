package com.assignmentservice.dto;

import com.assignmentservice.model.Priority;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentRequest {


    @NotBlank(message = "Ticket ID is mandatory")
	private String ticketId;


    @NotBlank(message = "Agent ID is mandatory")
	private String agentId;

    private Priority priority; // HIGH / MEDIUM / LOW
}
