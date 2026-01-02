package com.ticker_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//this is communicated through the feing client
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAssignedAgent {

	@NotBlank
	private String agentId;
}
