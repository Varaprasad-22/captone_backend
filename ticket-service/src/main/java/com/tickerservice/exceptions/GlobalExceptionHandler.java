package com.tickerservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	// this is meant for the ticket not found from custom class
	@ExceptionHandler(TicketNotFoundException.class)
	public ResponseEntity<String> handleTicketNotFound(TicketNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	// this is which ticket state change issuue see this is based on enum it is
	// fixed
	@ExceptionHandler(InvalidTicketstateException.class)
	public ResponseEntity<String> handleInvalidState(InvalidTicketstateException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	// this is if validations failed
	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {

		String message = ex.getBindingResult().getFieldError().getDefaultMessage();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}

	//for the exceptions
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneric(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}
}
