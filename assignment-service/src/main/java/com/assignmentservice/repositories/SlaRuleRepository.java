package com.assignmentservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignmentservice.model.Priority;
import com.assignmentservice.model.SlaRule;

@Repository
public interface SlaRuleRepository extends JpaRepository<SlaRule, Long> {

	Optional<SlaRule> findByPriorityAndActiveTrue(Priority priority);
}
