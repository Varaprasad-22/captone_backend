package com.assignmentservice.dto;

import com.assignmentservice.model.SlaStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTicketStatusRequest {
    private SlaStatus status;
}
