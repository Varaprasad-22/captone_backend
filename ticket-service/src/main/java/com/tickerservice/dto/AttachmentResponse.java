package com.tickerservice.dto;

import java.time.LocalDateTime;


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
