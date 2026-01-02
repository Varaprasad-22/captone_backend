package com.assignmentService.service;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.assignmentService.dto.AgentStatusCount;
import com.assignmentService.dto.AgentWorkLoadResponse;
import com.assignmentService.dto.AssignmentRequest;
import com.assignmentService.dto.ReAssignment;

import jakarta.validation.Valid;

public interface AssignmentService {

	String assign(AssignmentRequest req, String assignedBy);

	List<AgentWorkLoadResponse> getAgentWorkload(String agentId);

	 List<AgentStatusCount> getAllAgentsWorkload();

	 String  reassign(String assignedBy, @Valid ReAssignment request); 
}
