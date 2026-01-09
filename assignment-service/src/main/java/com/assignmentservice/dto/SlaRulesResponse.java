package com.assignmentservice.dto;

import com.assignmentservice.model.Priority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlaRulesResponse {
	
    private Priority priority;

    private int responseMinutes;
    private int resolutionHours;
    private boolean active;
	
}
