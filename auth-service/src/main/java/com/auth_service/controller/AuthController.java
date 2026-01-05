package com.auth_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth_service.dto.AdminCreationRequest;
import com.auth_service.dto.AllUsersResponse;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.LoginResponse;
import com.auth_service.dto.RegisterRequest;
import com.auth_service.dto.UserInfoResponse;
import com.auth_service.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
		authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/admin/register")
	public ResponseEntity<String> registerUser(@Valid @RequestBody AdminCreationRequest request) {
		authService.adminCreateUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
	}

//	@GetMapping("/getAll")
//	public ResponseEntity<List<AllUsersResponse>> getAll() {
//		return ResponseEntity.ok(authService.getAllUsers());
//	}

	// see paggin based get all users
	@GetMapping("/getAll")
	public ResponseEntity<Page<AllUsersResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "role.name") String sortBy,
			@RequestParam(defaultValue = "ASC") String direction) {
		return ResponseEntity.ok(authService.getAllUsers(page, size, sortBy, direction));
	}

	@GetMapping("/getAgents")
	public ResponseEntity<Page<AllUsersResponse>> getAllAgents(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "role.name") String sortBy,
			@RequestParam(defaultValue = "ASC") String direction) {
		return ResponseEntity.ok(authService.getAllAgents(page, size, sortBy, direction));
	}
	
	
	// this is for internal services to assignment service
	@GetMapping("/getEmail/{userId}")
	public ResponseEntity<UserInfoResponse> getByUserId(@PathVariable String userId) {
		return ResponseEntity.ok(authService.getUsersById(userId));
	}

	// for deactivating users
	@PutMapping("/deactivate/{userId}")
	public ResponseEntity<Void> deactivateUser(@PathVariable String userId, @RequestBody Map<String, Boolean> request) {
		authService.activateDeactivateUser(userId, request.get("active"));
		return ResponseEntity.noContent().build();
	}
}
