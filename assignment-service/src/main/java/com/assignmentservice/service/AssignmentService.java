package com.assignmentservice.service;

import java.util.List;

import com.assignmentservice.dto.AgentStatusCount;
import com.assignmentservice.dto.AgentWorkLoadResponse;
import com.assignmentservice.dto.AssignmentRequest;
import com.assignmentservice.dto.ReAssignment;

import jakarta.validation.Valid;

public interface AssignmentService {

	String assign(AssignmentRequest req, String assignedBy);

	List<AgentWorkLoadResponse> getAgentWorkload(String agentId);

	 List<AgentStatusCount> getAllAgentsWorkload();

	 String  reassign(String assignedBy, @Valid ReAssignment request);

	 String getManagerId(String ticketId); 
}
