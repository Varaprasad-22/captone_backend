package com.ticker_service.dto;

import com.ticker_service.model.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketStatusUpdateRequest {
    private String ticketId;
    private TicketStatus status;
}
