package com.assignmentService.service;

import java.util.List;

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
	public List<SlaEventResponse> getAllEvents() {
		// TODO Auto-generated method stub
        return eventRepository.findAll().stream().map(slaEvent->{
        SlaEventResponse response=new SlaEventResponse();
        response.setAgentId(slaEvent.getAgentId());
        response.setAssignmentId(slaEvent.getAssignmentId());
        response.setEventId(slaEvent.getEventId());
        response.setEventType(slaEvent.getEventType());
        response.setOccurredAt(slaEvent.getOccurredAt());
        response.setRemarks(slaEvent.getRemarks());
        response.setTicketId(slaEvent.getTicketId());
        return response;
        }).toList();
        }
	@Override
	public List<SlaEventResponse> getEventsByAgent(String agentId) {
		// TODO Auto-generated method stub
		  return eventRepository.findByAgentId(agentId);
	}

}
