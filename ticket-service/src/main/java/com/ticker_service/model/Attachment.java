package com.ticker_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "attachments")
public class Attachment {

    @Id
    private String id; //note--- ObjectId

    private String ticketId; //notee-- I reused Ticket ObjectId (String)
    private String fileName;
    private String fileUrl;
    private String fileType;

    private String uploadedBy;
    private LocalDateTime uploadedAt;
}
