package com.auth_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auth_service.model.Erole;
import com.auth_service.model.Role;
import com.auth_service.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {

	Optional<Users> findByEmail(String email);

	Page<Users> findAll(Pageable pageable);

	Page<Users> findByRole_Name(Erole role, Pageable pageable);

	Page<Users> findByRole(String role, Pageable pageable);

}
