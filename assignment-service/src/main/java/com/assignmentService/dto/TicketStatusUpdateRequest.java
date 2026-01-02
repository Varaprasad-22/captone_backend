package com.assignmentService.dto;

import com.assignmentService.model.TicketStatus;

import lombok.Data;

//see this is for changing response time and resolved time in slas;
@Data
public class TicketStatusUpdateRequest {
    private String ticketId;
    private TicketStatus status;
}
