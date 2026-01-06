package com.ticker_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.ticker_service.client.AssignmentClient;
import com.ticker_service.client.AuthClient;
import com.ticker_service.dto.*;
import com.ticker_service.exceptions.TicketNotFoundException;
import com.ticker_service.model.*;
import com.ticker_service.repository.AttachmentRepository;
import com.ticker_service.repository.CommentRepository;
import com.ticker_service.repository.TicketRepository;

class TicketServiceImplTest {

	@InjectMocks
	private TicketServiceImpl ticketService;

	@Mock
	private TicketRepository ticketRepository;
	@Mock
	private FileStorageService fileStorageService;
	@Mock
	private AttachmentRepository attachmentRepository;
	@Mock
	private NotificationPublisher publisher;
	@Mock
	private AssignmentClient assignmentClient;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private AuthClient authClient;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createTicket_success() {

		CreateTicketRequest request = new CreateTicketRequest("Login issue", "Unable to login into system",
				TicketCategory.SOFTWARE);

		Ticket savedTicket = new Ticket();
		savedTicket.setTicketId("T123");
		savedTicket.setTitle(request.getTitle());

		when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

		String result = ticketService.createTicket(request, null, "user1", "user@test.com");

		assertTrue(result.contains("Ticket Created"));
		verify(ticketRepository).save(any(Ticket.class));
		verify(publisher).publish(any(NotificationEvent.class), eq("ticket.created"));
	}

	@Test
	void updateStatus_success() {

		Ticket ticket = new Ticket();
		ticket.setTicketId("T1");
		ticket.setCreatedByUserId("user1");
		ticket.setTitle("Issue");

		when(ticketRepository.findById("T1")).thenReturn(Optional.of(ticket));
		when(authClient.getByUserId("user1"))
				.thenReturn(new UserInfoResponse("user1", "user@test.com", "Test User", true, "ROLE_USER"));

		ticketService.updateStatus("T1", TicketStatus.RESOLVED);

		verify(ticketRepository).save(ticket);
		verify(publisher).publish(any(NotificationEvent.class), eq("ticket.Updates"));
	}

	@Test
	void updateStatus_ticketNotFound() {

		when(ticketRepository.findById("T1")).thenReturn(Optional.empty());

		assertThrows(TicketNotFoundException.class, () -> ticketService.updateStatus("T1", TicketStatus.OPEN));
	}

	@Test
	void getAllOpenTickets_success() {

		when(ticketRepository.findByStatus(TicketStatus.OPEN)).thenReturn(List.of(new TicketResponse()));

		List<TicketResponse> result = ticketService.getAllOpenTickets();

		assertEquals(1, result.size());
	}

	@Test
	void getPerUserTickets_success() {

		when(ticketRepository.findByCreatedByUserId("user1")).thenReturn(List.of(new TicketResponse()));

		List<TicketResponse> tickets = ticketService.getPerUserTickets("user1");

		assertFalse(tickets.isEmpty());
	}

	@Test
	void updateAgentId_success() {

		Ticket ticket = new Ticket();
		ticket.setTicketId("T1");

		when(ticketRepository.findById("T1")).thenReturn(Optional.of(ticket));

		UpdateAssignedAgent request = new UpdateAssignedAgent();
		request.setAgentId("agent1");
		request.setPriority(Priority.HIGH);

		ticketService.updateAgentId("T1", request);

		assertEquals("agent1", ticket.getAssignedAgentId());
		assertEquals(TicketStatus.ASSIGNED, ticket.getStatus());
		verify(ticketRepository).save(ticket);
	}

	@Test
	void addComment_success() {

		Ticket ticket = new Ticket();
		ticket.setTicketId("T1");

		when(ticketRepository.findById("T1")).thenReturn(Optional.of(ticket));

		ticketService.addComment("T1", "user1", "test comment", false);

		verify(commentRepository).save(any(Comment.class));
	}

	@Test
	void getComments_success() {

		Comment comment = new Comment();
		comment.setCommentId("C1");
		comment.setText("Hello");

		when(commentRepository.findAllByTicketId("T1")).thenReturn(List.of(comment));

		List<CommentResponse> result = ticketService.getComments("T1");

		assertEquals(1, result.size());
		assertEquals("Hello", result.get(0).getText());
	}

	@Test
	void viewTicket_success() {

		Ticket ticket = new Ticket();
		ticket.setTicketId("T1");
		ticket.setTitle("Issue");

		when(ticketRepository.findById("T1")).thenReturn(Optional.of(ticket));

		TicketResponse response = ticketService.viewTicket("T1");

		assertNotNull(response);
		assertEquals("T1", response.getTicketId());
	}

	@Test
	void getUserDashboard_success() {

		when(ticketRepository.countByCreatedByUserId("user1")).thenReturn(10L);
		when(ticketRepository.countByCreatedByUserIdAndStatus("user1", TicketStatus.OPEN)).thenReturn(3L);
		when(ticketRepository.countByCreatedByUserIdAndStatus("user1", TicketStatus.INPROGRESS)).thenReturn(2L);
		when(ticketRepository.countByCreatedByUserIdAndAssignedAgentIdIsNotNull("user1")).thenReturn(4L);
		when(ticketRepository.countByCreatedByUserIdAndStatusIn(eq("user1"), anyList())).thenReturn(1L);

		UserDashboardResponse dashboard = ticketService.getUserDashboard("user1");

		assertEquals(10, dashboard.getTotal());
		assertEquals(3, dashboard.getOpen());
		assertEquals(2, dashboard.getInProgress());
		assertEquals(4, dashboard.getAssigned());
		assertEquals(1, dashboard.getClosed());
	}

	@Test
	void getAgentAllotedTickets_success() {

		when(ticketRepository.findByAssignedAgentId("agent1")).thenReturn(List.of(new TicketResponse()));

		List<TicketResponse> tickets = ticketService.getAgentAllotedTickets("agent1");

		assertEquals(1, tickets.size());
	}

	@Test
	void getAgentResolvedTickets_success() {

		when(ticketRepository.findByAssignedAgentIdAndStatusIn(eq("agent1"), anyList()))
				.thenReturn(List.of(new TicketResponse()));

		List<TicketResponse> tickets = ticketService.getAgentResolvedTickets("agent1");

		assertEquals(1, tickets.size());
	}

	@Test
	void getAllTickets_success() {

		Ticket ticket = new Ticket();
		ticket.setTicketId("T1");
		ticket.setTitle("Issue");

		when(ticketRepository.findAll()).thenReturn(List.of(ticket));

		List<TicketResponse> responses = ticketService.getAllTickets();

		assertEquals(1, responses.size());
		assertEquals("T1", responses.get(0).getTicketId());
	}
	
	@Test
	void updateStatus_escalated_success() {

	    Ticket ticket = new Ticket();
	    ticket.setTicketId("T1");
	    ticket.setCreatedByUserId("user1");
	    ticket.setTitle("Issue");

	    when(ticketRepository.findById("T1"))
	            .thenReturn(Optional.of(ticket));

	    UserInfoResponse user = new UserInfoResponse(
	            "user1", "user@test.com", "User", true, "ROLE_USER"
	    );

	    UserInfoResponse manager = new UserInfoResponse(
	            "mgr1", "manager@test.com", "Manager", true, "ROLE_MANAGER"
	    );

	    when(authClient.getByUserId("user1")).thenReturn(user);
	    when(assignmentClient.getManagerId("T1"))
	            .thenReturn(ResponseEntity.ok("mgr1"));
	    when(authClient.getByUserId("mgr1")).thenReturn(manager);

	    ticketService.updateStatus("T1", TicketStatus.ESCALATED);

	    verify(ticketRepository).save(ticket);
	}

	@Test
	void getAttachmentById_notFound() {

	    when(attachmentRepository.findById("A1"))
	            .thenReturn(Optional.empty());

	    RuntimeException ex = assertThrows(
	            RuntimeException.class,
	            () -> ticketService.getAttachmentById("A1")
	    );

	    assertEquals("Attachment not found", ex.getMessage());
	}
	
	@Test
	void getAttachmentById_success() {

	    Attachment attachment = new Attachment();
	    attachment.setId("A1");
	    attachment.setFileName("file.txt");

	    when(attachmentRepository.findById("A1"))
	            .thenReturn(Optional.of(attachment));

	    AttachmentResponse response =
	            ticketService.getAttachmentById("A1");

	    assertNotNull(response);
	    assertEquals("A1", response.getId());
	}

	@Test
	void getAttachmentsByTicketId_success() {

	    Attachment attachment = new Attachment();
	    attachment.setId("A1");
	    attachment.setFileName("file.txt");
	    attachment.setFileType("text/plain");
	    attachment.setFileUrl("/path/file.txt");
	    attachment.setTicketId("T1");
	    attachment.setUploadedBy("user1");
	    attachment.setUploadedAt(LocalDateTime.now());

	    when(attachmentRepository.findByTicketId("T1"))
	            .thenReturn(List.of(attachment));

	    List<AttachmentResponse> responses =
	            ticketService.getAttachmentsByTicketId("T1");

	    assertEquals(1, responses.size());
	    assertEquals("file.txt", responses.get(0).getFileName());
	    assertEquals("T1", responses.get(0).getTicketId());
	}

	@Test
	void createTicket_validFile_shouldSaveAttachment() {

	    CreateTicketRequest request = new CreateTicketRequest(
	            "Login issue",
	            "Unable to login into system",
	            TicketCategory.SOFTWARE
	    );

	    MultipartFile file = mock(MultipartFile.class);
	    when(file.isEmpty()).thenReturn(false);
	    when(file.getOriginalFilename()).thenReturn("test.txt");
	    when(file.getContentType()).thenReturn("text/plain");
	    when(fileStorageService.save(file)).thenReturn("/path/test.txt");

	    Ticket savedTicket = new Ticket();
	    savedTicket.setTicketId("T1");

	    when(ticketRepository.save(any(Ticket.class)))
	            .thenReturn(savedTicket);

	    ticketService.createTicket(
	            request,
	            List.of(file),
	            "user1",
	            "user@test.com"
	    );

	    verify(fileStorageService).save(file);
	    verify(attachmentRepository).saveAll(anyList());
	}

}
