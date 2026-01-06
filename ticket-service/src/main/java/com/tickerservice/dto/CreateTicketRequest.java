package com.tickerservice.dto;

import com.tickerservice.model.TicketCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTicketRequest {
	
	@NotBlank(message = "Title is mandatory")
	@Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
	private String title;
	
    @NotBlank(message = "Description is mandatory")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
	private String description;

	@NotNull(message = "Category required mandatory")
	private TicketCategory Category;
}
