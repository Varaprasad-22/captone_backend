package com.tickerservice.dto;

import com.tickerservice.model.TicketStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTicketStatusRequest {


    @NotNull(message = "Ticket status is mandatory")
    private TicketStatus status;
}

