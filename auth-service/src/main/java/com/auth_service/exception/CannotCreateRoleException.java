package com.auth_service.exception;

public class CannotCreateRoleException extends RuntimeException {
    public CannotCreateRoleException
    (String message) {
        super(message);
    }
}