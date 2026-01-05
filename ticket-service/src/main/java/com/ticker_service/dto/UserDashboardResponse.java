package com.ticker_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDashboardResponse {

    private long total;
    private long open;
    private long inProgress;
    private long assigned;
    private long closed;
}
