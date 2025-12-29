package com.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auth_service.model.Erole;
import com.auth_service.model.Role;
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{

	  Optional<Role>  findByName(Erole roles);

}
