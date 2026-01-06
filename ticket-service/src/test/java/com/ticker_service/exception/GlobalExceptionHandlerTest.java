package com.ticker_service.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tickerservice.exceptions.GlobalExceptionHandler;
import com.tickerservice.exceptions.InvalidTicketstateException;
import com.tickerservice.exceptions.TicketNotFoundException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

class GlobalExceptionHandlerTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(new TestExceptionController())
				.setControllerAdvice(new GlobalExceptionHandler()).build();
	}

	@Test
	void handleTicketNotFound_shouldReturn404() throws Exception {

		mockMvc.perform(get("/test/ticket-not-found")).andExpect(status().isNotFound())
				.andExpect(content().string("Ticket not found"));
	}

	@Test
	void handleInvalidTicketState_shouldReturn400() throws Exception {

		mockMvc.perform(get("/test/invalid-state")).andExpect(status().isBadRequest())
				.andExpect(content().string("Invalid ticket state"));
	}

	@Test
	void handleValidation_shouldReturn400() throws Exception {

		mockMvc.perform(get("/test/validation-error")).andExpect(status().isBadRequest())
				.andExpect(content().string("Name is mandatory"));
	}

	@Test
	void handleGenericException_shouldReturn500() throws Exception {

		mockMvc.perform(get("/test/generic-error")).andExpect(status().isInternalServerError())
				.andExpect(content().string("Something went wrong"));
	}

	@RestController
	static class TestExceptionController {

		@GetMapping("/test/ticket-not-found")
		public String ticketNotFound() {
			throw new TicketNotFoundException("Ticket not found");
		}

		@GetMapping("/test/invalid-state")
		public String invalidState() {
			throw new InvalidTicketstateException("Invalid ticket state");
		}

		@GetMapping("/test/validation-error")
		public String validationError(@Valid TestDto dto) {
			return "OK";
		}

		@GetMapping("/test/generic-error")
		public String genericError() {
			throw new RuntimeException("Something went wrong");
		}
	}

	static class TestDto {
		@NotBlank(message = "Name is mandatory")
		private String name;
	}
}
