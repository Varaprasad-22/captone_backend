package com.auth_service.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import com.auth_service.model.Erole;
import com.auth_service.model.Role;
import com.auth_service.repository.RoleRepository;

import jakarta.transaction.Transactional;

@Configuration
public class DataIntializer implements ApplicationRunner{

	private final RoleRepository roleRepo;
	
	public DataIntializer(RoleRepository roleRepo) {
		this.roleRepo=roleRepo;
	}
	
	@Override
	@Transactional
	public  void run(ApplicationArguments args) {
		for(Erole roles:Erole.values()) {
			roleRepo.findByName(roles)
			.orElseGet(()->{
				Role role=new Role();
				role.setName(roles);
				return roleRepo.save(role);
			});
		}
	}
}
