package com.ticker_service.dto;

import com.ticker_service.model.TicketStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketStatusUpdateRequest {

    @NotBlank(message = "Ticket ID is mandatory")
    private String ticketId;
    private TicketStatus status;
}
