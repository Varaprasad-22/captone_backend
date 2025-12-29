package com.auth_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth_service.dto.AdminCreationRequest;
import com.auth_service.dto.AllUsersResponse;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.RegisterRequest;
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
	public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
		authService.login(request);
		return ResponseEntity.ok("Login successful");
	}

	@PostMapping("/admin/register")
	public ResponseEntity<String> registerUser(@Valid @RequestBody AdminCreationRequest request) {
		authService.adminCreateUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
	}

	@GetMapping("/getAll")
	public ResponseEntity<List<AllUsersResponse>> getAll() {
		return ResponseEntity.ok(authService.getAllUsers());
	}
}
