package com.notificationService.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.notificationservice.service.EmailService;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void send_shouldSendEmailSuccessfully() {

        // given
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test email body";

        // when
        emailService.send(to, subject, body);

        // then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
