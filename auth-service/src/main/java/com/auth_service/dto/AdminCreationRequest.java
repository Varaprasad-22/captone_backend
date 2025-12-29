package com.auth_service.dto;

import com.auth_service.model.Erole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminCreationRequest {

	@NotBlank(message = "Name cannot be empty")
	@Size(min = 3, message = "Name must be between 3 characters")
	private String name;

	@NotBlank(message = "Email cannot be empty")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Password cannot be empty")
	@Size(min = 8, message = "Password must be at least 8 characters")
	private String password;

	@NotNull(message = "Role is required")
	private Erole role;
}
