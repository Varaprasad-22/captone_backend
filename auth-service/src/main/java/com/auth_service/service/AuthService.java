package com.auth_service.service;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.auth_service.dto.AdminCreationRequest;
import com.auth_service.dto.AllUsersResponse;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.RegisterRequest;
import com.auth_service.model.Users;

public interface AuthService {
	void register(RegisterRequest request);

	Users login(LoginRequest request);

	void adminCreateUser(AdminCreationRequest request);

	List<AllUsersResponse> getAllUsers();
}
