package com.assignmentService.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignmentService.model.Priority;
import com.assignmentService.model.SlaRule;

public interface SlaRuleRepository extends JpaRepository<SlaRule, Long> {

	Optional<SlaRule> findByPriorityAndActiveTrue(Priority priority);
}
