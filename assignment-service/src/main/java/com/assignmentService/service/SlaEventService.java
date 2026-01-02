package com.assignmentService.service;

import java.util.List;

import com.assignmentService.dto.SlaEventResponse;
import com.assignmentService.model.SlaEvent;

public interface SlaEventService {

    List<SlaEventResponse> getAllEvents();

    List<SlaEventResponse> getEventsByAgent(String agentId);
}
