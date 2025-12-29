package com.auth_service.service;

import com.auth_service.dto.AdminCreationRequest;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.RegisterRequest;
import com.auth_service.model.Users;

public interface AuthService {
	 void register(RegisterRequest request);
	 Users login(LoginRequest request);
	 void adminCreateUser(AdminCreationRequest request);
}
