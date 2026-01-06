package com.auth_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    private String userId;
    private String email;
    private String name;
    private boolean active;
    private String role;
}
