package com.assignmentservice.dto;

import com.assignmentservice.model.TicketStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//see this is for changing response time and resolved time in slas;
@Data
public class TicketStatusUpdateRequest {

    @NotBlank(message = "Ticket ID is mandatory")
    private String ticketId;
    private TicketStatus status;
}
