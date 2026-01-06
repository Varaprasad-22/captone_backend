package com.assignmentservice.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlaEventResponse {

	private Long eventId;

    private String assignmentId;
    private String ticketId;
    private String agentId;

    private String eventType; // ESCALATED / BREACHED
    private LocalDateTime occurredAt;

    private String remarks;
}
