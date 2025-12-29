package com.auth_service.dto;

import com.auth_service.model.Erole;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Erole role;
}
