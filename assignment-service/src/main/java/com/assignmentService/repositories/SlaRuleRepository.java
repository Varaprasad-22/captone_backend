package com.assignmentService.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignmentService.model.Priority;
import com.assignmentService.model.SlaRule;

@Repository
public interface SlaRuleRepository extends JpaRepository<SlaRule, Long> {

	Optional<SlaRule> findByPriorityAndActiveTrue(Priority priority);
}
