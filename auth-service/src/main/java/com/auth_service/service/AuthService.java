package com.auth_service.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.auth_service.dto.AdminCreationRequest;
import com.auth_service.dto.AllUsersResponse;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.LoginResponse;
import com.auth_service.dto.RegisterRequest;
import com.auth_service.dto.UserInfoResponse;
import com.auth_service.model.Erole;

public interface AuthService {
	void register(RegisterRequest request);

	LoginResponse login(LoginRequest request);

	void adminCreateUser(AdminCreationRequest request);

	List<AllUsersResponse> getAllUsers();

	UserInfoResponse getUsersById(String userId);

	void activateDeactivateUser(String userId, Boolean active);

	public Page<AllUsersResponse> getAllUsers(int page, int size, String sortBy, String direction);
	

	public Page<AllUsersResponse> getAllAgents(int page, int size, String sortBy, String direction);

	public Page<AllUsersResponse> getUsersByRole(String role, int page, int size, String sortBy, String direction);

	void updateUserRole(String userId, Erole role);
}
