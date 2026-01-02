package com.ticker_service.dto;

import com.ticker_service.model.TicketStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTicketStatusRequest {


    @NotNull(message = "Ticket status is mandatory")
    private TicketStatus status;
}

