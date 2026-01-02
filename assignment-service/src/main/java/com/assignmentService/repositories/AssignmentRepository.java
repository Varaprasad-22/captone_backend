package com.assignmentService.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.assignmentService.dto.AgentWorkLoadResponse;
import com.assignmentService.model.Assignment;
import com.assignmentService.model.Sla;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {

	@Query("""
			    SELECT a.status, COUNT(a)
			    FROM Assignment a
			    WHERE a.agentId = :agentId
			    GROUP BY a.status
			""")
	List<Object[]> countByStatus(@Param("agentId") String agentId);

	@Query("""
			    SELECT a.agentId, a.status, COUNT(a)
			    FROM Assignment a
			    GROUP BY a.agentId, a.status
			""")
	List<Object[]> getAllAgentWorkload();

	Optional<Assignment> findByTicketId(String ticketId);

}
