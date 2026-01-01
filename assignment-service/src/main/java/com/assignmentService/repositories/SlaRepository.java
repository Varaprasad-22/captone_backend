package com.assignmentService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignmentService.model.Sla;

@Repository
public interface SlaRepository extends JpaRepository<Sla, Long>{


	Sla[] findByBreachedFalse();

	Sla[] findActiveSlas();

}
