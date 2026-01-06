package com.assignmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentWorkLoadResponse {
    private String status;
    private Long count;
}