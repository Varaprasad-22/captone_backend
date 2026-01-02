package com.ticker_service.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Comment")
public class Comment {

	private String CommentId;
	private String ticketId;
	private String authorId;
	private String text;
	private boolean isInternal;
    private LocalDateTime createdAt;
}
