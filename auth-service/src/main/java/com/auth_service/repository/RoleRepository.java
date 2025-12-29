package com.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth_service.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{

}
