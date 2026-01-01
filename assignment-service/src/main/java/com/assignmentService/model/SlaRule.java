package com.assignmentService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "sla_rules")
@Data
public class SlaRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private Priority priority;

    private int responseMinutes;
    private int resolutionHours;
    private boolean active;
}
