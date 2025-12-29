package com.auth_service.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.RegisterRequest;
import com.auth_service.model.Role;
import com.auth_service.model.Users;
import com.auth_service.repository.RoleRepository;
import com.auth_service.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {
	@Autowired
    private  UserRepository userRepository;
    @Autowired
	private  RoleRepository roleRepository;
    private  PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
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

    public Users login(LoginRequest request) {

        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }


}
