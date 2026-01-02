package com.ticker_service.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ticker_service.dto.CreateTicketRequest;
import com.ticker_service.model.TicketStatus;

import jakarta.validation.Valid;

public interface TickerService {

	String createTicket(@Valid CreateTicketRequest request, List<MultipartFile> files, String userId, String userEmail);


    void updateStatus(String ticketId, TicketStatus status);
}
