package com.assignmentService.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.assignmentService.dto.SlaEventResponse;
import com.assignmentService.model.SlaEvent;

public interface SlaEventService {

    Page<SlaEventResponse> getAllEvents(
            int page,
            int size,
            String sortBy,
            String direction
    );
    List<SlaEventResponse> getEventsByAgent(String agentId);
}
