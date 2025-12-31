package com.ticker_service.dto;

import com.ticker_service.model.TicketCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTicketRequest {

	@NotBlank
	private String title;
	
	@NotBlank
	private String description;
	
	@NotNull(message = "Category required mandatory")
	private TicketCategory Category;
}
