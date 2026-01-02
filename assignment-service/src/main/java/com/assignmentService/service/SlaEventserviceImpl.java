package com.assignmentService.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.assignmentService.model.SlaEvent;
import com.assignmentService.repositories.SlaEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlaEventserviceImpl implements SlaEventService{
	
	private final SlaEventRepository eventRepository;

	@Override
	public List<SlaEvent> getAllEvents() {
		// TODO Auto-generated method stub
        return eventRepository.findAll();
	}

	@Override
	public List<SlaEvent> getEventsByAgent(String agentId) {
		// TODO Auto-generated method stub
		  return eventRepository.findByAgentId(agentId);
	}

}
