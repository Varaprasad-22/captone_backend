package com.ticker_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ticker_service.dto.CreateTicketRequest;
import com.ticker_service.model.Attachment;
import com.ticker_service.model.Ticket;
import com.ticker_service.model.TicketStatus;
import com.ticker_service.repository.TicketRepository;

import jakarta.validation.Valid;

@Service
public class TicketServiceImpl implements TickerService{

	@Autowired
	private TicketRepository ticketRepository;
	@Override
	public String createTicket(@Valid CreateTicketRequest request, List<MultipartFile> files, String userId) {
		Ticket ticket=new Ticket();
		ticket.setTitle(request.getTitle());
		ticket.setDescription(request.getDescription());
		ticket.setCategory(request.getCategory());
		ticket.setPriority("Low");
		ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedByUserId(userId);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket saved = ticketRepository.save(ticket);
        
        //trying to save files if not null
        //more over I will be saving them like in local system  
        List<Attachment> attachments=new ArrayList<>();
        if(files!=null) {
        	for(MultipartFile file:files) {
        		if(file.isEmpty())
        			continue;
        		
        	}
        }
		return null;
	}

}
