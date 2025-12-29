package com.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auth_service.model.Role;
import com.auth_service.model.Users;
@Repository
public interface UserRepository extends JpaRepository<Users, String>{

	Optional<Users> findByEmail(String email);

}
