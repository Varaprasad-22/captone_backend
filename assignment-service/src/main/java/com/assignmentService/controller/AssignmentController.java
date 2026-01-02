package com.assignmentService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignmentService.dto.AgentStatusCount;
import com.assignmentService.dto.AgentWorkLoadResponse;
import com.assignmentService.dto.AssignmentRequest;
import com.assignmentService.dto.ReAssignment;
import com.assignmentService.service.AssignmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {
	
	@Autowired
	private AssignmentService assignmentService;

	@PostMapping("/assign")
	public ResponseEntity<String> assignTicket(@RequestHeader("X-USER-ID") String assignedBy,
			@Valid @RequestBody AssignmentRequest req) {

		return ResponseEntity.ok(assignmentService.assign(req, assignedBy));
	}
	
	//for getting the workload on each agent
	@GetMapping("/agents/{agentId}/workload")
	public ResponseEntity<List<AgentWorkLoadResponse>> getAgentWorkload(
	        @PathVariable String agentId) {

	    return ResponseEntity.ok(
	        assignmentService.getAgentWorkload(agentId)
	    );
	}
	
	//to see the workload on each 
	@GetMapping("/manager/workload")
	public ResponseEntity<List<AgentStatusCount>> getAllAgentsWorkload() {
	    return ResponseEntity.ok(assignmentService.getAllAgentsWorkload());
	}

	//reassignments of tickets
	@PostMapping("/reassign")
	public ResponseEntity<String> reassign(@RequestHeader("X-USER-ID") String assignedBy,
			@Valid @RequestBody ReAssignment request){
		return ResponseEntity.ok().body(assignmentService.reassign(assignedBy,request));
	}
}
