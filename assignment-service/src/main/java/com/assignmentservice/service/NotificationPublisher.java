package com.assignmentservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.assignmentservice.config.RabbitConfig;
import com.assignmentservice.dto.NotificationEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

	private final RabbitTemplate rabbitTemplate;

	public void publish(NotificationEvent event, String routingKey) {
		rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, event);
	}
}
