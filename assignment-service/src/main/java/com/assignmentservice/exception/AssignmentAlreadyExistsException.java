package com.assignmentservice.exception;

public class AssignmentAlreadyExistsException extends RuntimeException {
	public AssignmentAlreadyExistsException(String message) {
		super(message);
	}
}