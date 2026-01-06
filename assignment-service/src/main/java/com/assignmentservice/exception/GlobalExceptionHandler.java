package com.assignmentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class GlobalExceptionHandler {

	// if the assignmnet id not found exceptions
	@ExceptionHandler(AssignmentNotFoundException.class)
	public ResponseEntity<String> handleAssignmentNotFound(AssignmentNotFoundException ex) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	// if agent is inactive state
	@ExceptionHandler(AgentUnavailableException.class)
	public ResponseEntity<String> handleAgentUnavailable(AgentUnavailableException ex) {

		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	// if asssignment already is assigned to some one u can reassign but cannot assign
	@ExceptionHandler(AssignmentAlreadyExistsException.class)
	public ResponseEntity<String> handleAssignmentAlreadyExists(AssignmentAlreadyExistsException ex) {

		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	// if validations missed
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {

		String message = ex.getBindingResult().getFieldError().getDefaultMessage();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

	// internal or all others
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneric(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}
}
