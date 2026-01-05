package com.assignmentService.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.assignmentService.dto.SlaEventResponse;
import com.assignmentService.model.SlaEvent;
import com.assignmentService.repositories.SlaEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlaEventserviceImpl implements SlaEventService{
	
	private final SlaEventRepository eventRepository;

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
		// TODO Auto-generated method stub
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

}
