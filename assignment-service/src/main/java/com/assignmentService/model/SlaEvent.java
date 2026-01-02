package com.assignmentService.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sla_events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlaEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String assignmentId;
    private String ticketId;
    private String agentId;

    private String eventType; // ESCALATED / BREACHED
    private LocalDateTime occurredAt;

    private String remarks;
}
