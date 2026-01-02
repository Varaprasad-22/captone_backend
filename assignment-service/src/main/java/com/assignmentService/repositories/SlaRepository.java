package com.assignmentService.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.assignmentService.model.Assignment;
import com.assignmentService.model.Sla;

@Repository
public interface SlaRepository extends JpaRepository<Sla, Long> {

	Sla[] findByBreachedFalse();

	@Query("""
			    SELECT s FROM Sla s
			    WHERE s.breached = false
			      AND s.resolvedAt IS NULL
			""")
	Sla[] findActiveSlas();

	Optional<Sla> findByAssignmentId(String assignmentId);

}
