package com.auth_service.dto;

import com.auth_service.model.Erole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllUsersResponse {

	private String userId;
	private String name;
	private String email;
	private Erole role;
	private boolean isActive;
}
