package com.auth_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth_service.dto.AdminCreationRequest;
import com.auth_service.dto.AllUsersResponse;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.LoginResponse;
import com.auth_service.dto.NotificationEvent;
import com.auth_service.dto.RegisterRequest;
import com.auth_service.dto.UserInfoResponse;
import com.auth_service.exception.CannotCreateRoleException;
import com.auth_service.exception.InvalidCredentialsException;
import com.auth_service.exception.RoleNotFoundException;
import com.auth_service.exception.UserAlreadyExistsException;
import com.auth_service.exception.UserDisabledException;
import com.auth_service.exception.UserNotFoundException;
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
			throw new UserAlreadyExistsException("User already exists");
		}

		Role role = roleRepository.findByName(Erole.ROLE_USER)
				.orElseThrow(() -> new RoleNotFoundException("Role not found"));

		Users user = new Users();
		user.setUserId(UUID.randomUUID().toString());
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(role);
		user.setActive(true);

		userRepository.save(user);

		// here afte saving this event is done where it placees it in rabbt queue
		NotificationEvent event = new NotificationEvent("USER_CREATED", user.getEmail(), "Welcome",
				"Hello " + user.getName() + ", your account is ready.");

		publisher.publish(event, "user.created");

	}

	public LoginResponse login(LoginRequest request) {

		Users user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid credentials"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid credentials");
		}

		if (!user.isActive()) {
			throw new UserDisabledException("Your account is deactivated. Please contact admin.");
		}
		String token = jwtutils.generateToken(user.getUserId(), user.getRole().getName().name(), user.getName(),
				user.getEmail());
		return new LoginResponse(token);
	}

	public void adminCreateUser(AdminCreationRequest request) {

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new UserAlreadyExistsException("User already exists");
		}
		if (request.getRole() == Erole.ROLE_ADMIN) {
			throw new CannotCreateRoleException("Cannot create ADMIN users");
		}

		Role role = roleRepository.findByName(request.getRole())
				.orElseThrow(() -> new RoleNotFoundException("Role not found"));

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

	@Override
	public UserInfoResponse getUsersById(String userId) {
		// TODO Auto-generated method stub
		Users user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No USer FOund"));

		return new UserInfoResponse(user.getUserId(), user.getEmail(), user.getName(), user.isActive(),
				user.getRole().getName().name());
	}

	@Override
	public void activateDeactivateUser(String userId, Boolean active) {
		// TODO Auto-generated method stub
		Users user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
		if ("ROLE_ADMIN".equals(user.getRole().getName().name())) {
			throw new UserDisabledException("You cannot disable an user");
		}
		if (user.isActive()) {
			if (active) {
				throw new UserDisabledException("User Already in Active State");
			}
		} else {
			if (!active) {
				throw new UserDisabledException("User Already  Deactivated");
			}
		}
		user.setActive(active);
		userRepository.save(user);
	}

	@Override
	public Page<AllUsersResponse> getAllUsers(int page, int size, String sortBy, String direction) {
		// TODO Auto-generated method stub

		Sort sort = direction.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

		PageRequest pageable = PageRequest.of(page, size, sort);

		return userRepository.findAll(pageable).map(user -> new AllUsersResponse(user.getUserId(), user.getName(),
				user.getEmail(), user.getRole().getName(), user.isActive()));
	}

	@Override
	public Page<AllUsersResponse> getUsersByRole(String role, int page, int size, String sortBy, String direction) {

		Sort sort = direction.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);
		Erole erole;
		try {
			erole = Erole.valueOf(role);
		} catch (IllegalArgumentException e) {
			throw new RoleNotFoundException("Invalid role: " + role);
		}

		Page<Users> usersPage = userRepository.findByRole_Name(erole, pageable);

		return usersPage.map(user -> new AllUsersResponse(user.getUserId(), user.getName(), user.getEmail(),
				user.getRole().getName(), user.isActive()));
	}

	public Page<AllUsersResponse> getAllAgents(int page, int size, String sortBy, String direction) {
		// TODO Auto-generated method stub

		Sort sort = direction.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

		PageRequest pageable = PageRequest.of(page, size, sort);

		// "ROLE_AGENT" to the custom repository method
		return userRepository.findByRole_Name(Erole.ROLE_AGENT, pageable)
				.map(user -> new AllUsersResponse(user.getUserId(), user.getName(), user.getEmail(),
						user.getRole().getName(), user.isActive()));
	}

	@Override
	public void updateUserRole(String userId, Erole newRole) {

		Users user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

		// Do not allow changing ADMIN role
		if (user.getRole().getName() == Erole.ROLE_ADMIN) {
			throw new CannotCreateRoleException("Cannot modify ADMIN role");
		}

		// Prevent setting ADMIN role
		if (newRole == Erole.ROLE_ADMIN) {
			throw new CannotCreateRoleException("Cannot assign ADMIN role");
		}

		Role roleEntity = roleRepository.findByName(newRole)
				.orElseThrow(() -> new RoleNotFoundException("Role not found"));

		// Same role check
		if (user.getRole().getName() == newRole) {
			throw new IllegalStateException("User already has this role");
		}

		user.setRole(roleEntity);
		userRepository.save(user);
	}

}
