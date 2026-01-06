package com.ticker_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickerservice.controller.TicketController;
import com.tickerservice.dto.*;
import com.tickerservice.model.TicketStatus;
import com.tickerservice.service.TickerService;

class TicketControllerTest {

	private MockMvc mockMvc;

	@Mock
	private TickerService ticketService;

	@InjectMocks
	private TicketController ticketController;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
		objectMapper = new ObjectMapper();
	}

	@Test
	void createTicket_success() throws Exception {

		String ticketJson = """
				{
				  "title": "Login issue",
				  "description": "Unable to login into the system",
				  "Category": "SOFTWARE"
				}
				""";

		MockMultipartFile ticket = new MockMultipartFile("ticket", "", "application/json", ticketJson.getBytes());

		MockMultipartFile files = new MockMultipartFile("files", "test.txt", "text/plain", "sample data".getBytes());

		mockMvc.perform(multipart("/tickets/create").file(ticket).file(files).header("X-USER-ID", "user123")
				.header("X-USER-EMAIL", "user@test.com")).andExpect(status().isCreated());
	}

	@Test
	void updateTicketStatus_success() throws Exception {

		UpdateTicketStatusRequest request = new UpdateTicketStatusRequest();
		request.setStatus(TicketStatus.RESOLVED);

		mockMvc.perform(put("/tickets/{ticketId}/status", "T123").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isNoContent());
	}

	@Test
	void getAllOpenTickets_success() throws Exception {

		mockMvc.perform(get("/tickets/getAllOpenTickets")).andExpect(status().isOk());
	}

	@Test
	void getPerUserTickets_success() throws Exception {

		mockMvc.perform(get("/tickets/{userId}/getTickets", "user123")).andExpect(status().isOk());
	}

	@Test
	void getAgentAllotedTickets_success() throws Exception {

		mockMvc.perform(get("/tickets/{agentId}/getAgentTickets", "agent1")).andExpect(status().isOk());
	}

	@Test
	void getAgentResolvedTickets_success() throws Exception {

		mockMvc.perform(get("/tickets/{agentId}/getAgentResolvedTickets", "agent1")).andExpect(status().isOk());
	}

	@Test
	void getAllTickets_success() throws Exception {

		mockMvc.perform(get("/tickets/getAllTickets")).andExpect(status().isOk());
	}

	@Test
	void updateAssignedAgent_success() throws Exception {

		UpdateAssignedAgent request = new UpdateAssignedAgent();
		request.setAgentId("agent123");

		mockMvc.perform(put("/tickets/{ticketId}/updateAgentId", "T123").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isNoContent());
	}

	@Test
	void addComment_success() throws Exception {

		String commentJson = """
				{
				  "text": "This is a comment",
				  "isInternal": false
				}
				""";

		mockMvc.perform(post("/tickets/{ticketId}/comments", "T123").header("X-USER-ID", "user123")
				.contentType(MediaType.APPLICATION_JSON).content(commentJson)).andExpect(status().isCreated());
	}

	@Test
	void getComments_success() throws Exception {

		mockMvc.perform(get("/tickets/{ticketId}/getComments", "T123")).andExpect(status().isOk());
	}

	@Test
	void viewASpecificTicket_success() throws Exception {

		mockMvc.perform(get("/tickets/{ticketId}/getTicket", "T123")).andExpect(status().isOk());
	}

	@Test
	void getUserDashboard_success() throws Exception {

		mockMvc.perform(get("/tickets/my/dashboard").header("X-USER-ID", "user123")).andExpect(status().isOk());
	}
	
	@Test
	void viewAttachment_success() throws Exception {

	    // create a temp file
	    Path tempFile = Files.createTempFile("test-attachment", ".txt");
	    Files.writeString(tempFile, "sample file content");

	    AttachmentResponse attachment = new AttachmentResponse();
	    attachment.setId("A1");
	    attachment.setFileName("test.txt");
	    attachment.setFileType("text/plain");
	    attachment.setFileUrl(tempFile.toString());

	    when(ticketService.getAttachmentById("A1"))
	            .thenReturn(attachment);

	    mockMvc.perform(
	            get("/tickets/attachments/view/{attachmentId}", "A1")
	    )
	    .andExpect(status().isOk())
	    .andExpect(header().string(
	            HttpHeaders.CONTENT_DISPOSITION,
	            "inline; filename=\"test.txt\""
	    ))
	    .andExpect(content().string("sample file content"));
	}

	@Test
	void getAttachments_success() throws Exception {

	    AttachmentResponse attachment = new AttachmentResponse();
	    attachment.setId("A1");
	    attachment.setFileName("file.txt");
	    attachment.setFileType("text/plain");
	    attachment.setFileUrl("/tmp/file.txt");
	    attachment.setTicketId("T1");

	    when(ticketService.getAttachmentsByTicketId("T1"))
	            .thenReturn(List.of(attachment));

	    mockMvc.perform(
	            get("/tickets/{ticketId}/attachments", "T1")
	    )
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$[0].id").value("A1"))
	    .andExpect(jsonPath("$[0].fileName").value("file.txt"));
	}

}
