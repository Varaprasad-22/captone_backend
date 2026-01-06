package com.ticker_service.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.tickerservice.config.RabbitConfig;
import com.tickerservice.dto.NotificationEvent;
import com.tickerservice.service.NotificationPublisher;

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

		NotificationEvent event = new NotificationEvent("TICKET_CREATED", "user@test.com", "Ticket Created",
				"Ticket created successfully");

		String routingKey = "ticket.created";

		notificationPublisher.publish(event, routingKey);

		verify(rabbitTemplate).convertAndSend(RabbitConfig.Exchange, routingKey, event);
	}
}
