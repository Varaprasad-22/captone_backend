package com.assignmentService.service;

import java.util.List;

import com.assignmentService.model.SlaEvent;

public interface SlaEventService {

    List<SlaEvent> getAllEvents();

    List<SlaEvent> getEventsByAgent(String agentId);
}
