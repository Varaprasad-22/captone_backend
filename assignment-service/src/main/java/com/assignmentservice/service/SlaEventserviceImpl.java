package com.assignmentservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.assignmentservice.dto.SlaEventResponse;
import com.assignmentservice.dto.SlaRulesResponse;
import com.assignmentservice.model.SlaEvent;
import com.assignmentservice.model.SlaRule;
import com.assignmentservice.repositories.SlaEventRepository;
import com.assignmentservice.repositories.SlaRuleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlaEventserviceImpl implements SlaEventService{
	
	private final SlaEventRepository eventRepository;
	private final SlaRuleRepository ruleRepository;

	@Override
    public Page<SlaEventResponse> getAllEvents(
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return eventRepository.findAll(pageable)
                .map(this::toResponse);
    }

	@Override
	public List<SlaEventResponse> getEventsByAgent(String agentId) {
	
		  return eventRepository.findByAgentId(agentId) .stream()	
		            .map(this::toResponse)
		            .toList();
	}
	 private SlaEventResponse toResponse(SlaEvent event) {
	        return new SlaEventResponse(
	                event.getEventId(),
	                event.getAssignmentId(),
	                event.getTicketId(),
	                event.getAgentId(),
	                event.getEventType(),
	                event.getOccurredAt(),
	                event.getRemarks()
	        );
	    }

	 @Override
	 public List<SlaRulesResponse> getSlaRules() {
		return ruleRepository.findAll().stream()
				.map(rule->new SlaRulesResponse(
						rule.getPriority(),
						rule.getResponseMinutes(),
						rule.getResolutionHours(),
						rule.isActive()
						)).toList();
	 }

	 @Override
	 public void updateSlaRules(List<SlaRulesResponse> ruleDtos) {
	     for (SlaRulesResponse dto : ruleDtos) {
	         SlaRule existingRule = ruleRepository.findByPriority(dto.getPriority())
	                 .orElseThrow(() -> new RuntimeException("SLA Rule not found for priority: " + dto.getPriority()));

	        
	         existingRule.setResponseMinutes(dto.getResponseMinutes());
	         existingRule.setResolutionHours(dto.getResolutionHours());
	         existingRule.setActive(dto.isActive());

	         ruleRepository.save(existingRule);
	     }
	 }
}
