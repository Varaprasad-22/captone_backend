package com.assignmentService.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.assignmentService.config.RabbitConfig;
import com.assignmentService.dto.NotificationEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

	private final RabbitTemplate rabbitTemplate;

	public void publish(NotificationEvent event, String routingKey) {
		rabbitTemplate.convertAndSend(RabbitConfig.Exchange, routingKey, event);
	}
}
