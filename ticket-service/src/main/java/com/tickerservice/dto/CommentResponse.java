package com.tickerservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
	private String CommentId;
	private String ticketId;
	private String authorId;
	private String text;
	private boolean isInternal;
	private LocalDateTime createdAt;
}
