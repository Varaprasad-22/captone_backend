package com.auth_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.auth_service.dto.*;
import com.auth_service.exception.*;
import com.auth_service.model.Erole;
import com.auth_service.model.Role;
import com.auth_service.model.Users;
import com.auth_service.repository.RoleRepository;
import com.auth_service.repository.UserRepository;
import com.auth_service.security.JwtUtil;

class AuthServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private NotificationPublisher publisher;

	@InjectMocks
	private AuthServiceImpl authService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}


	@Test
	void register_success() {

		RegisterRequest request = new RegisterRequest("User", "user@test.com", "password");

		Role role = new Role();
		role.setName(Erole.ROLE_USER);

		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
		when(roleRepository.findByName(Erole.ROLE_USER)).thenReturn(Optional.of(role));
		when(passwordEncoder.encode(anyString())).thenReturn("encoded");

		authService.register(request);

		verify(userRepository).save(any(Users.class));
		verify(publisher).publish(any(NotificationEvent.class), eq("user.created"));
	}

	@Test
	void register_userAlreadyExists() {

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new Users()));

		assertThrows(UserAlreadyExistsException.class,
				() -> authService.register(new RegisterRequest("A", "a@b.com", "p")));
	}

	@Test
	void login_success() {

		Users user = new Users();
		user.setUserId("U1");
		user.setEmail("user@test.com");
		user.setName("User");
		user.setPassword("encoded");
		user.setActive(true);

		Role role = new Role();
		role.setName(Erole.ROLE_USER);
		user.setRole(role);

		when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
		when(jwtUtil.generateToken(any(), any(), any(), any())).thenReturn("jwt-token");

		LoginResponse response = authService.login(new LoginRequest("user@test.com", "password"));

		assertEquals("jwt-token", response.getToken());
	}

	@Test
	void login_invalidPassword() {

		Users user = new Users();
		user.setPassword("encoded");
		user.setActive(true);

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		assertThrows(InvalidCredentialsException.class, () -> authService.login(new LoginRequest("a", "b")));
	}


	@Test
	void adminCreateUser_success() {

		AdminCreationRequest request = new AdminCreationRequest("Agent", "agent@test.com", "pass", Erole.ROLE_AGENT);

		Role role = new Role();
		role.setName(Erole.ROLE_AGENT);

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(roleRepository.findByName(Erole.ROLE_AGENT)).thenReturn(Optional.of(role));
		when(passwordEncoder.encode(anyString())).thenReturn("encoded");

		authService.adminCreateUser(request);

		verify(userRepository).save(any(Users.class));
	}

	@Test
	void adminCreateUser_adminRoleNotAllowed() {

		AdminCreationRequest request = new AdminCreationRequest("Admin", "a@a.com", "p", Erole.ROLE_ADMIN);

		assertThrows(CannotCreateRoleException.class, () -> authService.adminCreateUser(request));
	}

	@Test
	void getUsersById_success() {

		Users user = new Users();
		user.setUserId("U1");
		user.setEmail("user@test.com");
		user.setName("User");
		user.setActive(true);

		Role role = new Role();
		role.setName(Erole.ROLE_USER);
		user.setRole(role);

		when(userRepository.findById("U1")).thenReturn(Optional.of(user));

		UserInfoResponse response = authService.getUsersById("U1");

		assertEquals("user@test.com", response.getEmail());
	}


	@Test
	void activateDeactivateUser_success() {

		Users user = new Users();
		user.setActive(true);

		Role role = new Role();
		role.setName(Erole.ROLE_USER);
		user.setRole(role);

		when(userRepository.findById("U1")).thenReturn(Optional.of(user));

		authService.activateDeactivateUser("U1", false);

		verify(userRepository).save(user);
	}


	@Test
	void getAllUsers_paginated_success() {

		Users user = new Users();
		user.setUserId("U1");
		user.setName("User");
		user.setEmail("user@test.com");
		user.setActive(true);

		Role role = new Role();
		role.setName(Erole.ROLE_USER);
		user.setRole(role);

		Page<Users> page = new PageImpl<>(List.of(user));

		when(userRepository.findAll(any(PageRequest.class))).thenReturn(page);

		Page<AllUsersResponse> result = authService.getAllUsers(0, 10, "name", "ASC");

		assertEquals(1, result.getTotalElements());
	}

	@Test
	void getUsersByRole_success() {

		Users user = new Users();
		user.setRole(new Role());

		Page<Users> page = new PageImpl<>(List.of(user));

		when(userRepository.findByRole_Name(eq(Erole.ROLE_USER), any())).thenReturn(page);

		Page<AllUsersResponse> result = authService.getUsersByRole("ROLE_USER", 0, 10, "name", "ASC");

		assertEquals(1, result.getTotalElements());
	}

	@Test
	void getAllAgents_success() {

		when(userRepository.findByRole_Name(eq(Erole.ROLE_AGENT), any())).thenReturn(Page.empty());

		Page<AllUsersResponse> result = authService.getAllAgents(0, 10, "name", "ASC");

		assertEquals(0, result.getTotalElements());
	}

	@Test
	void updateUserRole_success() {

		Users user = new Users();
		Role oldRole = new Role();
		oldRole.setName(Erole.ROLE_USER);
		user.setRole(oldRole);

		Role newRole = new Role();
		newRole.setName(Erole.ROLE_AGENT);

		when(userRepository.findById("U1")).thenReturn(Optional.of(user));
		when(roleRepository.findByName(Erole.ROLE_AGENT)).thenReturn(Optional.of(newRole));

		authService.updateUserRole("U1", Erole.ROLE_AGENT);

		verify(userRepository).save(user);
	}
	@Test
	void getAllUsers_success() {

	    Role role = new Role();
	    role.setName(Erole.ROLE_USER);

	    Users user1 = new Users();
	    user1.setUserId("U1");
	    user1.setName("User One");
	    user1.setEmail("user1@test.com");
	    user1.setRole(role);
	    user1.setActive(true);

	    Users user2 = new Users();
	    user2.setUserId("U2");
	    user2.setName("User Two");
	    user2.setEmail("user2@test.com");
	    user2.setRole(role);
	    user2.setActive(false);

	    when(userRepository.findAll())
	            .thenReturn(List.of(user1, user2));

	    List<AllUsersResponse> result = authService.getAllUsers();

	    assertEquals(2, result.size());

	    AllUsersResponse first = result.get(0);
	    assertEquals("U1", first.getUserId());
	    assertEquals("User One", first.getName());
	    assertEquals("user1@test.com", first.getEmail());
	    assertEquals(Erole.ROLE_USER, first.getRole());
	    assertTrue(first.isActive());

	    AllUsersResponse second = result.get(1);
	    assertEquals("U2", second.getUserId());
	    assertFalse(second.isActive());

	    verify(userRepository).findAll();
	}

	@Test
	void updateUserRole_userNotFound() {

	    when(userRepository.findById("U1"))
	            .thenReturn(Optional.empty());

	    assertThrows(
	            UserNotFoundException.class,
	            () -> authService.updateUserRole("U1", Erole.ROLE_AGENT)
	    );

	    verify(userRepository).findById("U1");
	    verifyNoMoreInteractions(userRepository);
	}

	@Test
	void updateUserRole_sameRole_shouldFail() {

	    Role agentRole = new Role();
	    agentRole.setName(Erole.ROLE_AGENT);

	    Users user = new Users();
	    user.setUserId("U1");
	    user.setRole(agentRole);

	    when(userRepository.findById("U1"))
	            .thenReturn(Optional.of(user));

	    when(roleRepository.findByName(Erole.ROLE_AGENT))
	            .thenReturn(Optional.of(agentRole));

	    IllegalStateException ex = assertThrows(
	            IllegalStateException.class,
	            () -> authService.updateUserRole("U1", Erole.ROLE_AGENT)
	    );

	    assertEquals("User already has this role", ex.getMessage());
	}

	@Test
	void updateUserRole_roleNotFound() {

	    Role userRole = new Role();
	    userRole.setName(Erole.ROLE_USER);

	    Users user = new Users();
	    user.setUserId("U1");
	    user.setRole(userRole);

	    when(userRepository.findById("U1"))
	            .thenReturn(Optional.of(user));

	    when(roleRepository.findByName(Erole.ROLE_AGENT))
	            .thenReturn(Optional.empty());

	    assertThrows(
	            RoleNotFoundException.class,
	            () -> authService.updateUserRole("U1", Erole.ROLE_AGENT)
	    );
	}
	@Test
	void updateUserRole_assignAdminRole_shouldFail() {

	    Role userRole = new Role();
	    userRole.setName(Erole.ROLE_USER);

	    Users user = new Users();
	    user.setUserId("U1");
	    user.setRole(userRole);

	    when(userRepository.findById("U1"))
	            .thenReturn(Optional.of(user));

	    CannotCreateRoleException ex = assertThrows(
	            CannotCreateRoleException.class,
	            () -> authService.updateUserRole("U1", Erole.ROLE_ADMIN)
	    );

	    assertEquals("Cannot assign ADMIN role", ex.getMessage());
	}
	@Test
	void updateUserRole_existingUserIsAdmin_shouldFail() {

	    Role adminRole = new Role();
	    adminRole.setName(Erole.ROLE_ADMIN);

	    Users adminUser = new Users();
	    adminUser.setUserId("U1");
	    adminUser.setRole(adminRole);

	    when(userRepository.findById("U1"))
	            .thenReturn(Optional.of(adminUser));

	    CannotCreateRoleException ex = assertThrows(
	            CannotCreateRoleException.class,
	            () -> authService.updateUserRole("U1", Erole.ROLE_AGENT)
	    );

	    assertEquals("Cannot modify ADMIN role", ex.getMessage());
	}

	@Test
	void activateDeactivateUser_userNotFound() {

	    when(userRepository.findById("U1"))
	            .thenReturn(Optional.empty());

	    assertThrows(
	            UserNotFoundException.class,
	            () -> authService.activateDeactivateUser("U1", false)
	    );

	    verify(userRepository).findById("U1");
	    verifyNoMoreInteractions(userRepository);
	}

	@Test
	void activateDeactivateUser_adminUser_shouldFail() {

	    Role adminRole = new Role();
	    adminRole.setName(Erole.ROLE_ADMIN);

	    Users admin = new Users();
	    admin.setUserId("U1");
	    admin.setRole(adminRole);
	    admin.setActive(true);

	    when(userRepository.findById("U1"))
	            .thenReturn(Optional.of(admin));

	    UserDisabledException ex = assertThrows(
	            UserDisabledException.class,
	            () -> authService.activateDeactivateUser("U1", false)
	    );

	    assertEquals("You cannot disable an user", ex.getMessage());
	}

	@Test
	void activateDeactivateUser_alreadyActive_shouldFail() {

	    Role userRole = new Role();
	    userRole.setName(Erole.ROLE_USER);

	    Users user = new Users();
	    user.setUserId("U1");
	    user.setRole(userRole);
	    user.setActive(true);

	    when(userRepository.findById("U1"))
	            .thenReturn(Optional.of(user));

	    UserDisabledException ex = assertThrows(
	            UserDisabledException.class,
	            () -> authService.activateDeactivateUser("U1", true)
	    );

	    assertEquals("User Already in Active State", ex.getMessage());
	}

	@Test
	void activateDeactivateUser_alreadyDeactivated_shouldFail() {

	    Role userRole = new Role();
	    userRole.setName(Erole.ROLE_USER);

	    Users user = new Users();
	    user.setUserId("U1");
	    user.setRole(userRole);
	    user.setActive(false);

	    when(userRepository.findById("U1"))
	            .thenReturn(Optional.of(user));

	    UserDisabledException ex = assertThrows(
	            UserDisabledException.class,
	            () -> authService.activateDeactivateUser("U1", false)
	    );

	    assertEquals("User Already  Deactivated", ex.getMessage());
	}

}
