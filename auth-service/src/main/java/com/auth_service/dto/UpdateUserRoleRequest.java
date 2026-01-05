package com.auth_service.dto;

import com.auth_service.model.Erole;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    private Erole role;
}