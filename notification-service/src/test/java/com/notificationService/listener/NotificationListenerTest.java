package com.notificationService.listener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.notificationservice.Listener.NotificationListener;
import com.notificationservice.dto.NotificationEvent;
import com.notificationservice.model.Notification;
import com.notificationservice.repository.NotificationRepository;
import com.notificationservice.service.EmailService;

class NotificationListenerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationRepository notificationRepo;

    @InjectMocks
    private NotificationListener notificationListener;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void consume_success_shouldSendEmailAndSaveNotification() {

        NotificationEvent event = new NotificationEvent(
                "TICKET_CREATED",
                "user@test.com",
                "Ticket Created",
                "Your ticket has been created"
        );

        when(notificationRepo.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        notificationListener.consume(event);

        verify(emailService).send(
                "user@test.com",
                "Ticket Created",
                "Your ticket has been created"
        );

        verify(notificationRepo).save(argThat(notification ->
                notification.getStatus().equals("SENT") &&
                notification.getRecipientEmail().equals("user@test.com") &&
                notification.getSubject().equals("Ticket Created") &&
                notification.getMessage().equals("Your ticket has been created") &&
                notification.getErrorMessage() == null
        ));
    }

    @Test
    void consume_failure_shouldSaveFailedNotification() {

        NotificationEvent event = new NotificationEvent(
                "TICKET_CREATED",
                "user@test.com",
                "Ticket Created",
                "Your ticket has been created"
        );

        doThrow(new RuntimeException("SMTP server down"))
                .when(emailService)
                .send(any(), any(), any());

        when(notificationRepo.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        notificationListener.consume(event);

        verify(emailService).send(any(), any(), any());

        verify(notificationRepo).save(argThat(notification ->
                notification.getStatus().equals("FAILED") &&
                notification.getErrorMessage().contains("SMTP server down")
        ));
    }
}
