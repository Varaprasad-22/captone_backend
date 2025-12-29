package com.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth_service.model.Users;

public interface UserRepository extends JpaRepository<Users, String>{

}
