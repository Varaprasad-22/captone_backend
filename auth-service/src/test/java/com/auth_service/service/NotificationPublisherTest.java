package com.auth_service.service;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.auth_service.config.RabbitConfig;
import com.auth_service.dto.NotificationEvent;


class NotificationPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificationPublisher notificationPublisher;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void publish_success() {

        NotificationEvent event = new NotificationEvent(
                "USER_CREATED",
                "user@test.com",
                "User Created",
                "USer created successfully"
        );

        String routingKey = "ticket.created";

        notificationPublisher.publish(event, routingKey);

        verify(rabbitTemplate)
                .convertAndSend(RabbitConfig.Exchange, routingKey, event);
    }
}
