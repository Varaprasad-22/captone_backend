package com.ticker_service.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class AttachmentResponse {
	    private String id; //note--- ObjectId

	    private String ticketId; //notee-- I reused Ticket ObjectId (String)
	    private String fileName;
	    private String fileUrl;
	    private String fileType;

	    private String uploadedBy;
	    private LocalDateTime uploadedAt;
}
