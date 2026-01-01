package com.assignmentService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignmentService.dto.AssignmentRequest;
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
}
