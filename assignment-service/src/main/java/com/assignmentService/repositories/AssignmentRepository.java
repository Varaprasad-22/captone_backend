package com.assignmentService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignmentService.model.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, String>{

}
