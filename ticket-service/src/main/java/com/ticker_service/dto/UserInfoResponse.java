package com.ticker_service.dto;

import lombok.Data;

@Data
public class UserInfoResponse {
    private String userId;
    private String email;
    private String name;
    private boolean active;
    private Erole role;

}