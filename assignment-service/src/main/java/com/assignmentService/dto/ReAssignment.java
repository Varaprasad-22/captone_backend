package com.assignmentService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReAssignment {


    @NotBlank(message = "Ticket ID is mandatory")
	private String ticketId;
	

    @NotBlank(message = "Agent ID is mandatory")
	private String newAgentId;
}
