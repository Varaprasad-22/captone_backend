package com.auth_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth_service.dto.AdminCreationRequest;
import com.auth_service.dto.AllUsersResponse;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.LoginResponse;
import com.auth_service.dto.NotificationEvent;
import com.auth_service.dto.RegisterRequest;
import com.auth_service.model.Erole;
import com.auth_service.model.Role;
import com.auth_service.model.Users;
import com.auth_service.repository.RoleRepository;
import com.auth_service.repository.UserRepository;
import com.auth_service.security.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtUtil jwtutils;
	@Autowired
	private NotificationPublisher publisher;

	public void register(RegisterRequest request) {

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new RuntimeException("User already exists");
		}

		Role role = roleRepository.findByName(Erole.ROLE_USER)
				.orElseThrow(() -> new RuntimeException("Role not found"));

		Users user = new Users();
		user.setUserId(UUID.randomUUID().toString());
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(role);
		user.setActive(true);

		userRepository.save(user);
		
		//here afte saving this event is done where it placees it in rabbt queue
		NotificationEvent event = new NotificationEvent(
		        "USER_CREATED",
		        user.getEmail(),
		        "Welcome",
		        "Hello " + user.getName() + ", your account is ready."
		);

		publisher.publish(event, "user.created");

	}

	public LoginResponse login(LoginRequest request) {

		Users user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid credentials"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}

		String token =jwtutils.generateToken(user.getUserId(), user.getRole().getName().name(),user.getName(),user.getEmail());
		return new LoginResponse(token);
	}

	public void adminCreateUser(AdminCreationRequest request) {

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new RuntimeException("User already exists");
		}
		if (request.getRole() == Erole.ROLE_ADMIN) {
			throw new RuntimeException("Cannot create ADMIN users");
		}

		Role role = roleRepository.findByName(request.getRole())
				.orElseThrow(() -> new RuntimeException("Role not found"));

		Users user = new Users();
		user.setUserId(UUID.randomUUID().toString());
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(role);
		user.setActive(true);

		userRepository.save(user);
	}

	@Override
	public List<AllUsersResponse> getAllUsers() {
		return userRepository.findAll().stream().map(user -> new AllUsersResponse(user.getUserId(), user.getName(),
				user.getEmail(), user.getRole().getName(), user.isActive())).toList();
	}

}
