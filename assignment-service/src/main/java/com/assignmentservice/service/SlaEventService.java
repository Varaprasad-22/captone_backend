package com.assignmentservice.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.assignmentservice.dto.SlaEventResponse;
import com.assignmentservice.dto.SlaRulesResponse;

public interface SlaEventService {

    Page<SlaEventResponse> getAllEvents(
            int page,
            int size,
            String sortBy,
            String direction
    );
    List<SlaEventResponse> getEventsByAgent(String agentId);
	List<SlaRulesResponse> getSlaRules();
	void updateSlaRules(List<SlaRulesResponse> ruleDtos);
}
