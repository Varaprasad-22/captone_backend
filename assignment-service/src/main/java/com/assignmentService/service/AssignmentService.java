package com.assignmentService.service;

import com.assignmentService.dto.AssignmentRequest;

public interface AssignmentService {

	String assign(AssignmentRequest req, String assignedBy);
}
