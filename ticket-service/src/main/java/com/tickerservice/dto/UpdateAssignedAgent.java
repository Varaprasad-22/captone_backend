package com.tickerservice.dto;

import com.tickerservice.model.Priority;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//this is communicated through the feing client
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAssignedAgent {

    @NotBlank(message = "Agent ID is mandatory")
    private String agentId;
	
	private Priority priority;
}
