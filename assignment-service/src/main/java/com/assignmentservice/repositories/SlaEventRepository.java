package com.assignmentservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignmentservice.model.SlaEvent;

@Repository
public interface SlaEventRepository extends JpaRepository<SlaEvent, Long> {

	List<SlaEvent> findByAgentId(String agentId);
	
}
