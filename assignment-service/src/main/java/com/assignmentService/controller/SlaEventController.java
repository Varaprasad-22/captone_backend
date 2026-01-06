package com.assignmentService.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignmentService.dto.SlaEventResponse;
import com.assignmentService.service.SlaEventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sla-events")
@RequiredArgsConstructor
public class SlaEventController {

	private final SlaEventService slaEventService;

	// see this edpoints helps in dashboards wise if needed like we can get the all
	// events history or paticular agent history and keep only the neeeded people or
	// remove based on risk they posses

	@GetMapping
	public ResponseEntity<Page<SlaEventResponse>> getAllEvents(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "occurredAt") String sortBy,
	        @RequestParam(defaultValue = "DESC") String direction
	) {
	    return ResponseEntity.ok(
	            slaEventService.getAllEvents(page, size, sortBy, direction)
	    );
	}


	@GetMapping("/agent/{agentId}")
	public ResponseEntity<List<SlaEventResponse>> getAgentEvents(@PathVariable String agentId) {

		return ResponseEntity.ok(slaEventService.getEventsByAgent(agentId));
	}
}
