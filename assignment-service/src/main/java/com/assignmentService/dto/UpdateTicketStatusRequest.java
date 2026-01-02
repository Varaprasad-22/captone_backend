package com.assignmentService.dto;

import com.assignmentService.model.SlaStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTicketStatusRequest {
    private SlaStatus status;
}
