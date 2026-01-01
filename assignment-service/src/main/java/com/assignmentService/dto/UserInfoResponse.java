package com.assignmentService.dto;


import lombok.Data;

@Data
public class UserInfoResponse {
    private String userId;
    private String email;
    private String name;
    private Erole role;
}