package com.auth_service.service;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.auth_service.dto.AdminCreationRequest;
import com.auth_service.dto.AllUsersResponse;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.LoginResponse;
import com.auth_service.dto.RegisterRequest;
import com.auth_service.dto.UserInfoResponse;
import com.auth_service.model.Users;

public interface AuthService {
	void register(RegisterRequest request);

	LoginResponse login(LoginRequest request);

	void adminCreateUser(AdminCreationRequest request);

	List<AllUsersResponse> getAllUsers();

	UserInfoResponse getUsersById(String userId);
}
