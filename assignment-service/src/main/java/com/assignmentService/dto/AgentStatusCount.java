package com.assignmentService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentStatusCount {
    private String agentId;
    private String status;
    private Long count;
}
