package com.auth_service.dto;

import com.auth_service.model.Erole;
import com.auth_service.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    private String userId;
    private String email;
    private String name;
    private boolean active;
    private Erole role;
}
